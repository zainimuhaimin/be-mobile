package id.co.hilmi.bemobile.model.parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "error_code")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCode implements Serializable {
    private static final long serialVersionUID = 6042355850858375041L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "source_system")
    private String sourceSystem;

    @Column(name = "en_message")
    private String englishMessage;

    @Column(name = "in_message")
    private String indonesiaMessage;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;
}
