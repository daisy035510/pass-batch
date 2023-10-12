package com.fastcampus.pass.repository.packaze;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBatchTest
@ActiveProfiles("test")
public class PackageRepositoryTest {

    PackageRepository packageRepository;

    @Test
    public void test_save() {

        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디 챌린지 PT 12주");
        packageEntity.setPeriod(84);

        // when
        packageRepository.save(packageEntity);

        // then
        assertNotNull(packageEntity.getPackageSeq());
    }
}