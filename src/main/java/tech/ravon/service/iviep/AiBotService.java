/**
 * Copyright 2025 Astronavigate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.ravon.service.iviep;

import java.io.IOException;

public interface AiBotService {

    /**
     * 定义回调接口，用于处理流式生成中的每个文本块。AiService
     * 这是 Service 层向 Controller 层传递数据的方式。
     */
    interface TextStreamCallback {
        void onNewText(String textChunk);
        void onComplete(); // 流完成时调用
        void onError(Throwable error); // 流发生错误时调用
    }

    /**
     * 调用 Llama.cpp 服务器的 /stream_generate 接口进行流式文本生成。
     * 此方法在单独的线程中执行 HTTP 请求和流处理。
     *AiService
     * @param sessionId 唯一标识符，用于跟踪和中断特定流（可以是用户的session ID或请求ID）
     * @param prompt 提示词
     * @param maxTokens 最大生成token数
     * @param callback 每个接收到的文本块的回调函数，以及完成/错误回调
     */
    void streamGenerateText(String sessionId, String prompt, int maxTokens, TextStreamCallback callback);

    /**
     * 调用 Llama.cpp 服务器的 /interrupt_stream 接口中断流式生成。
     * 此中断是通知 FastAPI 后端停止生成。
     *
     * @return 成功或失败消息的 JSON 字符串
     */
    String interruptLlamaStream();

    /**
     * 尝试通过 Java 线程中断机制中断在当前Java后端运行的特定流式线程，
     * 并同时通知 Llama.cpp 后端中断生成。
     *
     * @param sessionId 要中断的流的ID
     * @return 如果找到并中断了线程，则返回true；否则返回false。
     */
    boolean interruptClientStream(String sessionId);
}