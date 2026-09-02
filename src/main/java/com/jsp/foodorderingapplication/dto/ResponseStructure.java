package com.jsp.foodorderingapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseStructure <T>{
	
	
         private Integer statusCode;
         private String message;
         private T data;
}
