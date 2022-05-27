package com.shop.service;
import com.shop.domain.model.TokenInfo;
import com.shop.domain.request.User;
import com.shop.mapper.UserMapper;
import com.shop.response.Response;
import com.shop.util.JWTUtil;
import com.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JWTUtil jwtUtil;

    private final String TOKEN_PREFIX="Bearer";

    @Transactional(propagation = Propagation.REQUIRED, transactionManager = "transactionManager", rollbackFor = Exception.class)
    public int createUser(User user) {
        return userMapper.insertUser(user);
    }

    public Response refreshToken(String refreshToken, String accessToken) {
        if (accessToken == null || refreshToken == null || refreshToken.isEmpty() || accessToken.isEmpty()) {
            badRequestException("토큰 값을 전달 해주세요");
        }

        refreshToken = refreshToken.replace("Bearer ", "");
        accessToken = accessToken.replace("Bearer ", "");

        // 토큰이 유효한지 확인 후 토큰에서 디코딩 후 이메일 값 추출
        String email = findTokenToEmail(accessToken);

        Response response = new Response();
        // 이메일 값으로 redis에서 refresh 키 반환
        String redisToken = redisUtil.getRedisRefreshToken(email);
        if (redisToken == null || !redisToken.equals(refreshToken)) {
            badRequestException("유효하지 않는 토큰 입니다.");
        } else {
            // 토큰 생성
            TokenInfo tokenInfo = jwtUtil.createToken(email);
            // 토큰 업데이트
            redisUtil.saveRedisRefreshToken(tokenInfo);

            tokenInfo.setRefreshToken("Bearer " + tokenInfo.getRefreshToken());
            tokenInfo.setAccessToken("Bearer " + tokenInfo.getAccessToken());
            response.setResponse("refresh 토큰 생성", tokenInfo);
        }
        return response;
    }

    public Response logout(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            badRequestException("토큰 값을 전달 해주세요");
        }

        accessToken = accessToken.replace("Bearer ", "");
        String email = jwtUtil.getAccessTokenToExpiredEmail(accessToken);
        if (email == null) {
            badRequestException("유효하지 않는 토큰 입니다.");
        }

        if (redisUtil.getRedisRefreshToken(email) == null) {
            badRequestException("유효하지 않는 토큰 입니다.");
        } else {
            redisUtil.deleteRefreshToken(email);
            redisUtil.saveLogOutToken(accessToken, jwtUtil.getExpired(accessToken));
        }
        return new Response("로그인 성공");
    }

    public Response getUserInfo(String token) {
        Response response = new Response();
        String email = findTokenToEmail(token);
        response.setResponse("", userMapper.getUserInfo(email));
        return response;
    }

    public Response checkSignedEmail(String email) {
        if(email==null || email.isEmpty()){
            badRequestException("검색할 이메일을 전달해주세요");
        }
        Response response = new Response();
        User user= getFindByEmail(email);
        if(user==null){
            response.setResponse("가입되지 않은 이메일 입니다.", false);
        }else{
            response.setResponse("가입된 이메일이 있습니다.", true);
        }

        return response;
    }

    public boolean isLogOutToken(String accessToken) {
        return redisUtil.isLogOutToken(accessToken);
    }

    public void saveRedisRefreshToken(TokenInfo tokenInfo) {
        redisUtil.saveRedisRefreshToken(tokenInfo);
    }

    public User getFindByEmail(String email) {
        return userMapper.getFindByEmail(email);
    }

    public TokenInfo createToken(String email) {
        return jwtUtil.createToken(email);
    }

    public String getAccessTokenToEmail(String token) {
        return jwtUtil.getAccessTokenToEmail(token);
    }

    private String findTokenToEmail(String accessToken) {
        String email = jwtUtil.getAccessTokenToExpiredEmail(accessToken);
        if (email == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "");
        }
        return email;
    }

    public String getHeaderAuthorization(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION);
        if (token == null || !token.startsWith(TOKEN_PREFIX)) {
            return null;
        } else {
            return token.replace(TOKEN_PREFIX, "");
        }
    }

    private void badRequestException(String msg){
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, msg);
    }
}
