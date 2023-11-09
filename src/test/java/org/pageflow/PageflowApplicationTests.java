package org.pageflow;

import org.junit.jupiter.api.Test;
import org.pageflow.domain.book.service.SelectiveLISOptimizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PageflowApplicationTests {

	@Autowired
	private SelectiveLISOptimizer selectiveLISOptimizer;
	
	@Test
	void findSelectiveLIS(){
		// 1, 2, 3, 4, 5, 6, 101, 102, 103, 104 : 10개
		// 1, 2, 3, 4, 5, 6, 10, 11, 12 : 9개
		List<Integer> sequence = List.of(5, 2, 8, 6, 3, 11, 7, 10, 9, 1, 4, 12, 14, 13, 16, 15, 18, 17, 20, 19, 21, 23, 22, 24, 25);
		List<Integer> expected = List.of(2, 3, 7, 9, 12, 13, 15, 17, 19, 21, 22, 24, 25);
		
		List<Integer> result = selectiveLISOptimizer.findSelectiveLIS(sequence);
		
		System.out.println(result);
		assertEquals(expected.size(), result.size());
		
		if(expected.equals(result)){
			System.out.println("완벽이 일치하는 배열!!!");
		} else {
			System.out.println("기대와는 다르지만 최대 길이인 LIS를 잘 구했음!!!");
		}
	}

}
