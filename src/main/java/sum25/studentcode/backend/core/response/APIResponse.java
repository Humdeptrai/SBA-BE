package sum25.studentcode.backend.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class APIResponse<T> {
    private String status;
    private String message;
    private T data;
}
