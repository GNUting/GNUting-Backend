package gang.GNUtingBackend.chat.controller;

import com.google.protobuf.Api;
import gang.GNUtingBackend.chat.dto.ChatRequestDto;
import gang.GNUtingBackend.chat.dto.ChatResponseDto;
import gang.GNUtingBackend.chat.dto.ChatRoomResponseDto;
import gang.GNUtingBackend.chat.service.ChatRoomService;
import gang.GNUtingBackend.chat.service.ChatService;
import gang.GNUtingBackend.response.ApiResponse;
import gang.GNUtingBackend.user.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ChatRoomService chatRoomService;

    // /pub/chatRoom/{chatRoomId}
    @MessageMapping("/chatRoom/{chatRoomId}")
    @Operation(summary = "채팅 메시지 보내기", description = "해당 채팅방에 채팅 메시지를 보냅니다")
    public ResponseEntity<ApiResponse<ChatResponseDto>> sendMessage(
            @RequestHeader("Authorization") String token,
            @RequestBody ChatRequestDto chatRequestDto,
            @PathVariable Long chatRoomId) {

        String email = tokenProvider.getUserEmail(token.substring(7));

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(chatService.sendMessage(chatRequestDto, chatRoomId, email)));
    }

    @GetMapping("/chatRoom/{chatRoomId}/chats")
    @Operation(summary = "채팅 조회 API", description = "해당 채팅방의 모든 메시지를 조회합니다.")
    public ResponseEntity<ApiResponse<List<ChatResponseDto>>> getChatList(
            @RequestHeader("Authorization") String token,
            @PathVariable Long chatRoomId) {

        String email = tokenProvider.getUserEmail(token.substring(7));

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(chatService.findAllChatByChatRoomId(chatRoomId, email)));
    }

    @GetMapping("/chatRoom")
    @Operation(summary = "채팅방 조회 API", description = "사용자가 참여중인 모든 채팅방을 조회한다.")
    public ResponseEntity<ApiResponse<List<ChatRoomResponseDto>>> getAllChatRooms(
            @RequestHeader("Authorization") String token) {
        String email = tokenProvider.getUserEmail(token.substring(7));

        return ResponseEntity.ok()
                .body(ApiResponse.onSuccess(chatRoomService.findChatRoomsByUserEmail(email)));
    }


}
