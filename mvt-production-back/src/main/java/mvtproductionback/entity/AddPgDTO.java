package mvtproductionback.entity;

import lombok.Data;

/**
 * @Description
 * @Author wyjq
 * @Date 2023/1/6
 */

@Data
public class AddPgDTO {
    String ip;
    String port;
    String pgName;
    String userName;
    String password;
}
