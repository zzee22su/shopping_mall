package com.shop.service;
import com.shop.domain.model.TokenInfo;
import com.shop.domain.request.User;
import com.shop.mapper.UserMapper;
import com.shop.response.CustomException;
import com.shop.response.Response;
import com.shop.response.ResponseData;
import com.shop.util.JWTUtil;
import com.shop.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import javax.servlet.http.HttpServletRequest;

import static com.shop.response.ErrorCode.*;
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
    public ResponseEntity<ResponseData> createUser(User user) {
        User findUser=getFindByEmail(user.getEmail());
        if(findUser!=null){
            throw new CustomException(INVALID_SIGNUP_EMAIL);
        }

        int error=userMapper.insertUser(user);

        Response response = Response.getNewInstance();
        if(error > 0){
            return response.createResponseEntity("회원 가입 성공",true);
        }else{
            return response.createResponseEntity("회원 가입 실패",false);
        }
    }

    public ResponseEntity<ResponseData> refreshToken(String refreshToken, String accessToken) {
        if (accessToken == null || refreshToken == null || refreshToken.isEmpty() || accessToken.isEmpty()) {
            throw new CustomException(INVALID_TOKEN);
        }

        refreshToken = refreshToken.replace("Bearer ", "");
        accessToken = accessToken.replace("Bearer ", "");

        // 토큰이 유효한지 확인 후 토큰에서 디코딩 후 이메일 값 추출
        String email = findTokenToEmail(accessToken);

        // 이메일 값으로 redis에서 refresh 키 반환
        String redisToken = redisUtil.getRedisRefreshToken(email);
        if (redisToken == null || !redisToken.equals(refreshToken)) {
            throw new CustomException(INVALID_REFRESH_TOKEN);
        } else {
            // 토큰 생성
            TokenInfo tokenInfo = jwtUtil.createToken(email);
            // 토큰 업데이트
            redisUtil.saveRedisRefreshToken(tokenInfo);

            tokenInfo.setRefreshToken("Bearer " + tokenInfo.getRefreshToken());
            tokenInfo.setAccessToken("Bearer " + tokenInfo.getAccessToken());
            return Response.getNewInstance().createResponseEntity("refresh 토큰 생성", tokenInfo);
        }
    }

    public ResponseEntity<ResponseData> logout(String accessToken) {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new CustomException(INVALID_TOKEN);
        }

        accessToken = accessToken.replace("Bearer ", "");
        String email = findTokenToEmail(accessToken);

        if (redisUtil.getRedisRefreshToken(email) == null) {
            throw new CustomException(INVALID_EXPIRED_REFRESH_TOKEN);
        } else {
            redisUtil.deleteRefreshToken(email);
            redisUtil.saveLogOutToken(accessToken, jwtUtil.getExpired(accessToken));
        }
        return Response.getNewInstance().createResponseEntity("로그아웃 되었습니다.",true);
    }

    public ResponseEntity<ResponseData> getUserInfo(String token) {
        Response response = new Response();
        String email = findTokenToEmail(token);
        return Response.getNewInstance().createResponseEntity("", userMapper.getUserInfo(email));
    }

    public ResponseEntity<ResponseData> checkSignedEmail(String email) {
        if(email==null || email.isEmpty()){
            throw new CustomException(INVALID_EMAIL);
        }
        Response response = Response.getNewInstance();
        User user= getFindByEmail(email);
        if(user==null){
            return response.createResponseEntity("가입되지 않은 이메일 입니다.", false);
        }else{
            return  response.createResponseEntity("가입된 이메일이 있습니다.", true);
        }
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
            throw new CustomException(INVALID_TOKEN);
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
}
