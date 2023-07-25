package com.study.batch.passbatch.repository.packaze;

import com.sun.xml.bind.v2.runtime.reflect.Lister;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



@Slf4j
@SpringBootTest
@ActiveProfiles("test") // application-test.yml
class PackageRepositoryTest {

    @Autowired
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

    @Test
    public void test_findByCreatedAtAfter() {

        // given
        LocalDateTime dateTime = LocalDateTime.now().minusMinutes(1);

        PackageEntity packageEntity1 = new PackageEntity();
        packageEntity1.setPackageName("학생 전용 3개월");
        packageEntity1.setPeriod(90);
        packageRepository.save(packageEntity1);

        PackageEntity packageEntity2 = new PackageEntity();
        packageEntity2.setPackageName("학생 전용 6개월");
        packageEntity2.setPeriod(180);
        packageRepository.save(packageEntity2);

        // when
        final List<PackageEntity> packageEntities = packageRepository.findByCreatedAtAfter(dateTime,
                PageRequest.of(0, 1, Sort.by("packageSeq").descending()));

        // then
        assertEquals(1, packageEntities.size());
        assertEquals(packageEntity2.getPackageSeq(), packageEntities.get(0).getPackageSeq());
    }

    @Test
    public void test_updateCountAndPeriod() {

        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("바디프로필 이벤트 4개월");
        packageEntity.setPeriod(90);
        packageRepository.save(packageEntity);

        // when
        // JPQL로 구현
        int updatedCount = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(), 30, 120);
        final PackageEntity updatedPackageEntity = packageRepository.findById(packageEntity.getPackageSeq()).get();

        // then
        assertEquals(1, updatedCount);
        assertEquals(30, updatedPackageEntity.getCount());
        assertEquals(120, updatedPackageEntity.getPeriod());

    }


    @Test
    public void test_delete() {

        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("삭제할 이용권");
        packageEntity.setCount(1);
        PackageEntity newPackageEntity = packageRepository.save(packageEntity);

        // when
        packageRepository.deleteById(newPackageEntity.getPackageSeq());

        // then
        assertTrue(packageRepository.findById(newPackageEntity.getPackageSeq()).isEmpty());
    }
}