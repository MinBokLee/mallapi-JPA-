package org.zerock.mallapi.service;

import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;

public interface TodoService {

    Long register(TodoDTO todoDto);
    
    TodoDTO get(Long tno);

    void modify (TodoDTO todoDto);

    void remove(Long tno);

    
    // 목록 데이터의 처리는 PageRequestDTO 타입으로 파라이터를 처리하고, 
    // PageResponseDTO 타입을 리턴 타입으로 지정한다.
    PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO);

}

