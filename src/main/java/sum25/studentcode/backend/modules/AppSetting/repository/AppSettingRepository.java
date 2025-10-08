package sum25.studentcode.backend.modules.AppSetting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sum25.studentcode.backend.model.AppSetting;

public interface AppSettingRepository extends JpaRepository<AppSetting, Long> {
}