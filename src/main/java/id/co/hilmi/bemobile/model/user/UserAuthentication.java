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
@Table(name = "user_authentication")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthentication implements Serializable {

    private static final long serialVersionUID = 6042355850858375041L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_profile_id")
    private Long userProfileId;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "failed_login_attempts")
    private String failedLoginAttempts;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

}
