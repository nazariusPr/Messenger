package com.example.Server.dto.general;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class ExceptionDto {
  private String message;
  @JsonIgnore private String timeStamp;
  @JsonIgnore private String trace;
  @JsonIgnore private String path;

  public ExceptionDto(Map<String, Object> errorAttributes) {
    setPath((String) errorAttributes.get("path"));
    setMessage((String) errorAttributes.get("message"));
    setTimeStamp(errorAttributes.get("timestamp").toString());
    setTrace((String) errorAttributes.get("trace"));
  }
}
