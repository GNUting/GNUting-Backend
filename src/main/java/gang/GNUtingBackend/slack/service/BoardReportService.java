package gang.GNUtingBackend.slack.service;

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.header;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.composition.TextObject;
import gang.GNUtingBackend.board.entity.Board;
import gang.GNUtingBackend.board.repository.BoardRepository;
import gang.GNUtingBackend.exception.handler.BoardHandler;
import gang.GNUtingBackend.exception.handler.SlackHandler;
import gang.GNUtingBackend.response.code.status.ErrorStatus;
import gang.GNUtingBackend.slack.dto.BoardReportRequestDto;
import gang.GNUtingBackend.user.domain.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardReportService {

    @Value(value = "${slack.token}")
    private String token;
    @Value(value = "${slack.channel.monitor}")
    private String channel;

    private final BoardRepository boardRepository;

    public void postReport(BoardReportRequestDto boardReportRequestDto) throws IOException {

        Board board = boardRepository.findById(boardReportRequestDto.getBoardId())
                .orElseThrow(() -> new BoardHandler(ErrorStatus.BOARD_NOT_FOUND));

        User user = board.getUserId();

        // Slack 메세지 보내기
        try{
            List<TextObject> textObjects = new ArrayList<>();
            textObjects.add(markdownText("*이름:*\n" + user.getName()));
            textObjects.add(markdownText("*신고 날짜:*\n" + boardReportRequestDto.getCreatedDate()));
            textObjects.add(markdownText("*신고 글 제목:*\n" + board.getTitle()));
            textObjects.add(markdownText("*신고 사유:*\n" + boardReportRequestDto.getReportCategory()));
            textObjects.add(markdownText("*신고 내용:*\n" + boardReportRequestDto.getReportReason()));

            MethodsClient methods = Slack.getInstance().methods(token);
            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                    .channel(channel)
                    .blocks(asBlocks(
                            header(header -> header.text(plainText(user.getName() + "님이 문의를 남겨주셨습니다!"))),
                            divider(),
                            section(section -> section.fields(textObjects)
                            ))).build();

            methods.chatPostMessage(request);
        } catch (SlackApiException | IOException e) {
            throw new SlackHandler(ErrorStatus.CANNOT_SEND_MESSAGE);
        }
    }
}
