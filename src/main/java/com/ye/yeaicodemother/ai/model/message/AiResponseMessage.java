package com.ye.yeaicodemother.ai.model.message;

import dev.langchain4j.data.message.AiMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AiResponseMessage extends StreamMessage {

    private String data;

    public AiResponseMessage(String data){
       super(StreamMessageTypeEnum.AI_RESPONSE.getValue());
       this.data = data;

    }
}
