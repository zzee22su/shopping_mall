package com.shop;

import com.shop.mapper.FileMapper;
import com.shop.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShopApplicationTests {

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private FileMapper fileMapper;

	@Test
	void contextLoads() {
		Long id=45L;
		fileMapper.deleteFileGroup(id);
	}
}
