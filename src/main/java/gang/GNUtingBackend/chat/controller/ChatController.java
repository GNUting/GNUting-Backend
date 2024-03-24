package gang.GNUtingBackend.chat.controller;

import gang.GNUtingBackend.chat.dto.ChatRequestDto;
import gang.GNUtingBackend.chat.dto.ChatResponseDto;
import gang.GNUtingBackend.chat.service.ChatService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChatController {

    private final TokenProvider tokenProvider;
    private final ChatService chatService;

    @MessageMapping("/chatRoom/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatResponseDto>> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatRequestDto chatRequestDto,
            @PathVariable Long chatRoomId) {

        String email = tokenProvider.getUserEmail(token.substring(7));

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(chatService.sendMessage(chatRequestDto, chatRoomId, email)));
    }

    public ResponseEntity<ApiResponse<List<ChatResponseDto>>> getChatList(
            @RequestHeader("Authorization") String token,
            @PathVariable Long chatRoomId) {

        String email = tokenProvider.getUserEmail(token.substring(7));

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(chatService.findAllChatByChatRoomId(chatRoomId, email)));
    }
}
