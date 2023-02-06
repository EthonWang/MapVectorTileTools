package mvtproductionback.entity.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
//实现Serializable，序列化
public class JsonResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code=0;
    private String msg="success";
    private T data;

}
