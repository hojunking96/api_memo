package com.example.api_memo;

import com.example.api_memo.location.entity.Location;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;


@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    @Value("${resources.location}")
    private String resourceLocation;

    private final EntityManager em;

    @PostMapping("/init")
    @Transactional
    public String resetRegionList() {
        String fileLocation = resourceLocation + "/regionList.csv"; // 설정파일에 설정된 경로 뒤에 붙인다
        Path path = Paths.get(fileLocation);
        URI uri = path.toUri();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new UrlResource(uri).getInputStream()))
        ) {
            String line = br.readLine(); // head 떼기
            while ((line = br.readLine()) != null) {
                String[] splits = line.split(",");
                if (splits.length == 0) {
                    break;
                }
                em.persist(new Location(Long.parseLong(splits[0]), splits[1], splits[2],
                        Integer.parseInt(splits[3]), Integer.parseInt(splits[4])));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "오류 발생.";
        }
        return "초기화 성공";
    }
}