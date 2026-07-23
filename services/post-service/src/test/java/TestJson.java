import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.social_media.postservice.application.dto.PostResponse;

public class TestJson {
    public static void main(String[] args) throws Exception {
        PostResponse p = new PostResponse();
        p.setLikeCount(5);
        p.setLiked(true);
        p.setCommentCount(10);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        System.out.println(mapper.writeValueAsString(p));
    }
}
