package sba.project.sba_gralde.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sba.project.sba_gralde.model.AppSetting;

@Repository
public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
    boolean existsBySettingKey(String settingKey);
}