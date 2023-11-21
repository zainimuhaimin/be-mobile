package id.co.hilmi.bemobile.model.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "user_role")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole implements Serializable {

    private static final long serialVersionUID = 6042355850858375041L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_profile_id")
    private String userProfileId;

    @Column(name = "role_id")
    private String roleId;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
