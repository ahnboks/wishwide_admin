package com.wishwide.wishwide.persistence.alarm;

import com.wishwide.wishwide.domain.AlarmTemplate;
import com.wishwide.wishwide.domain.Product;
import org.springframework.data.repository.CrudRepository;

public interface CustomAlarmTemplateRepository extends CrudRepository<AlarmTemplate, Long>, CustomAlarmTemplate {

}
