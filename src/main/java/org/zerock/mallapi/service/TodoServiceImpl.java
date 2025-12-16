package org.zerock.mallapi.service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Todo;
import org.zerock.mallapi.dto.PageRequestDTO;
import org.zerock.mallapi.dto.PageResponseDTO;
import org.zerock.mallapi.dto.TodoDTO;
import org.zerock.mallapi.repository.TodoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Transactional
@Log4j2
@RequiredArgsConstructor // 생성자 자동 주입
public class TodoServiceImpl implements TodoService {

    private final ModelMapper modelMapper;

    private final TodoRepository todoRepository;

    @Override
    public Long register(TodoDTO todoDto){
        log.info(".........");

        Todo todo = modelMapper.map(todoDto, Todo.class);

        Todo saveTodo = todoRepository.save(todo);
        
        return saveTodo.getTno();
    }

    @Override
    public TodoDTO get(Long tno){
        Optional<Todo>  result = todoRepository.findById(tno);
        
        Todo todo = result.orElseThrow();

        TodoDTO dto = modelMapper.map(todo, TodoDTO.class);
        
        return dto;
    }

    @Override
    public void modify(TodoDTO todoDto){
        Optional<Todo> result = todoRepository.findById(todoDto.getTno());

        Todo todo = result.orElseThrow();

        todo.changeTitle(todoDto.getTitle());
        todo.changeDueDate(todoDto.getDueDate());
        todo.changeComplete(todoDto.isComplete());

        todoRepository.save(todo);

    }

    @Override
    public void remove(Long tno){
        todoRepository.deleteById(tno);
    }

    @Override
    public PageResponseDTO<TodoDTO> list(PageRequestDTO pageRequestDTO){
        
        Pageable pageable = 
            PageRequest.of(pageRequestDTO.getPage() -1, // 1페이지가 0이므로 주의
                            pageRequestDTO.getSize(),
                            Sort.by("tno").descending());

        Page<Todo> result = todoRepository.findAll(pageable);

        List<TodoDTO> dtoList = result.getContent().stream()
                                                    .map(todo -> modelMapper.map(todo, TodoDTO.class))
                                                    .collect(Collectors.toList());

        long totalCount = result.getTotalElements() ;

        PageResponseDTO<TodoDTO> responseDTO = 
         PageResponseDTO.<TodoDTO>withAll()
         .dtoList(dtoList)
         .pageRequestDTO(pageRequestDTO)
         .totalCount(totalCount)
         .build();

        return responseDTO;
    }

          
}
