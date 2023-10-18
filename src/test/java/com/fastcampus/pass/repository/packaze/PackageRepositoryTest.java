package com.fastcampus.pass.repository.packaze;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest  //@DataJpaTest
public class PackageRepositoryTest {

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

        PackageEntity packageEntity0 = new PackageEntity();
        packageEntity0.setPackageName("학생 전용 3개월");
        packageEntity0.setPeriod(90);
        packageRepository.save(packageEntity0);

        PackageEntity packageEntity1 = new PackageEntity();
        packageEntity1.setPackageName("학생 전용 6개월");
        packageEntity1.setPeriod(180);
        packageRepository.save(packageEntity1);

        // when
        List<PackageEntity> packageEntities  = packageRepository.findByCreatedAtAfter(dateTime, PageRequest.of(0, 1, Sort.by("packageSeq").descending()));

        // then
        assertEquals(1, packageEntities.size());
        assertEquals(packageEntities.get(0).getPackageSeq(), packageEntity1.getPackageSeq());
    }


//    @Test
//    public void test_updateCountAndPeriod() {
//
//        // given
//        PackageEntity packageEntity = new PackageEntity();
//        packageEntity.setPackageName("바디프로필 이벤트 4개월");
//        packageEntity.setPeriod(90);
//        packageRepository.save(packageEntity);
//
//        // when
//        // to-do : @Transactional 어노테이션 빼보기
//        int updateCount = packageRepository.updateCountAndPeriod(packageEntity.getPackageSeq(), 30, 120);
//        final PackageEntity updatedPackageEntity = packageRepository.findById(packageEntity.getPackageSeq()).get();
//
//        // then
//        assertEquals(1, updateCount);
//        assertEquals(30, updatedPackageEntity.getCount());
//        assertEquals(120, updatedPackageEntity.getPeriod());
//
//    }

    @Test
    public void test_delete() {

        // given
        PackageEntity packageEntity = new PackageEntity();
        packageEntity.setPackageName("제거할 이용권");
        packageEntity.setCount(1);
        PackageEntity packageEntity1 = packageRepository.save(packageEntity);

        // when
        packageRepository.deleteById(packageEntity1.getPackageSeq());

        // then
        assertTrue(packageRepository.findById(packageEntity1.getPackageSeq()).isEmpty());
    }
}