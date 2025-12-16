package org.zerock.mallapi.controller.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnails;

/**
 * 파일 데이터의 입출력을 담당하고, 프로그램이 시작되면 upload라는 이름의 폴더를 체크해서 자동으로 생성하도록
 * @PostConstruct를 이용하고, 파일의 업로드 작업은 saveFiles()로 작성한다.
 * 스프링 빈 생명주기(Spring Bean LifeCyle)
 * 스프링 컨테이너 생성 -> Bean 생성 -> 의존관계 주입 -> 초기화 콜백 -> 사용 -> 소멸콜백 -> 스프링 종료
 * 여기서 PostConstruct는 초기화 콜백에 사용됩니다.
 * @Value를 사용해서 주입한 인스턴스 필드의 값을 static 필드의 값으로 초기화 해주고 싶을 때 사용한다.
 * savaFiles()는 파리 저장 시에 중복된 이름이 저장되는 것을 막기 위해서 UUID로 중복이 발생하지 않도록 파일 이름을 구성한다.
 * 업로드 파일의 이름은 UUID값_파일명의 형태로 구성
 */

@Component
@Log4j2
@RequiredArgsConstructor
public class CustomFileUtil {

    @Value("${org.zerock.upload.path}")
    private String uploadPath;

    // 프로그램이 시작되면, upload라는 이름의 폴더를 체크해서 자동으로 생성하도록 @PostConstruct를 이용.
    @PostConstruct
    public void init(){
        File tempFolder = new File(uploadPath);

        if(tempFolder.exists() == false){
            tempFolder.mkdir();
        }

        uploadPath = tempFolder.getAbsolutePath();

        log.info("--------------------------");
        log.info("uploadPath :  " + uploadPath);

    }
    
    /**
     * 첨부파일은 수정이라는 개념이존재하지 않고, 기존 파일들을 삭제하고 새로운 파일로 대체하는 개념.
     * 삭제하는 기능이 필요하다. 파일이름을 기준으로 한 번에 여러 개의 파일을 삭제하는 기능을 CustomFileUtil 내부에 deleteFile()로 구현한다.
     * 파일의 삭제는 컨트롤러 계층 혹은 서비스 계층에서 데이터베이스 작업이 완료된 후에 필요없는 파일들을 삭제하는 용도로 처리할 때 사용.
     */
    public void deleteFiles(List<String> fileNames){

        if(fileNames == null || fileNames.size() == 0){
            return ;
        }
        fileNames.forEach(fileName ->{
            //썸네일이 있는지 확인하고 삭제
            String thumbnailFileName = "s_" + fileName;
            Path thumbnailPath =  Paths.get(uploadPath, thumbnailFileName);
            Path filePath = Paths.get(uploadPath, thumbnailFileName);
            
            try {
                Files.deleteIfExists(filePath);
                Files.deleteIfExists(thumbnailPath);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });

    }

    // getFile()은 파일의 종류마다 다르게 HTTTP 헤더 'Content-Type' 값을 생성해야 하기 떄문에
    // Files.probeContentType()으로 헤더 메시지를 생성한다.
    // ProductController에서 특정한 파일을 조회할 때, 사용한다.
    public ResponseEntity<Resource> getFile(String fileName){
        Resource resource = new FileSystemResource(uploadPath +  File.separator +  fileName);

        if(! resource.isReadable()){
                resource = new FileSystemResource(uploadPath + File.separator + "default.jpg");
        }

        HttpHeaders headers =new HttpHeaders();
        

        try {
        headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath() ));    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
        
    }

    // 이미지 파일 저장
    public List<String> saveFiles(List<MultipartFile> files)throws RuntimeException{
        if(files == null || files.size() == 0){
            return List.of();
        }    
        
        List<String> uploadNames = new ArrayList<>();
        for(MultipartFile multipartFile : files){
            String savedName = UUID.randomUUID().toString()+ "_" + multipartFile.getOriginalFilename();
            Path savePath = Paths.get(uploadPath, savedName);
            
            try{
                Files.copy(multipartFile.getInputStream(), savePath);
               
                String contentType = multipartFile.getContentType();
                if(contentType != null && contentType.startsWith("image")){
                    // 이미지 여부 확인
                    
                    Path thumbnailPath = Paths.get(uploadPath, "s_"+savedName);

                    Thumbnails.of(savePath.toFile())
                            .size(200,200)
                            .toFile(thumbnailPath.toFile());
                }

                // log.info("Saving to: " + savePath.toAbsolutePath()); 
                uploadNames.add(savedName);
                
            }catch(IOException e){
                    throw new RuntimeException(e.getMessage());
                }
        }// end of for
        return uploadNames;
    }
    
}
