package com.enigma.duitku.model.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommonResponse <T>{
    private Integer statusCode;
    private String message;
    private T data;
    private PagingResponse paging;
}
