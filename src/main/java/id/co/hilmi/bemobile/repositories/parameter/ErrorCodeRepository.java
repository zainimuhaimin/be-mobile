package id.co.hilmi.bemobile.repositories.parameter;

import id.co.hilmi.bemobile.dto.parameter.ErrorDetailInterface;
import id.co.hilmi.bemobile.model.parameter.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Long> {

    @Query(value = "SELECT ec.error_code as errorCode " +
            "ec.source_system as sourceSystem " +
            "ec.in_message as inMessage " +
            "ec.en_message as enMessage " +
            "FROM hilmi_data.error_code as ec " +
            "WHERE ec.error_code = :code AND ec.source_system = :sourceSystem", nativeQuery = true)
    ErrorDetailInterface getErrorDetail(@Param("code") String code,
                                        @Param("sourceSystem") String sourceSystem);
}
