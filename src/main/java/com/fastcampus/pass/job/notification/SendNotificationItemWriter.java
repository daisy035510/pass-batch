package com.fastcampus.pass.job.notification;

import com.fastcampus.pass.repository.notification.NotificationEntity;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> {
    @Override
    public void write(List<? extends NotificationEntity> items) throws Exception {

    }
}
