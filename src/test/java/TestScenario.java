import io.restassured.response.Response;
import org.hamcrest.*;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@TestMethodOrder(MethodOrderer.DisplayName.class)
public class TestScenario {

    BoardServices boardServices = new BoardServices();
    ListServices listServices = new ListServices();
    CardServices cardServices = new CardServices();
    static String boardId;
    static List<String> cardIds = new ArrayList<>();
    static String listId;

    static Stream<String> getStringStream() {
        return cardIds.stream();
    }


    @DisplayName("Step 1 - Create a new board with using Post service")
    @ParameterizedTest(name = "Create a board named as {0}")
    @ValueSource(strings = "TestBoard")
    public void createBoard(String boardName) {
       Response response = boardServices.createBoard("/1/boards/", boardName);
       response.then()
               .statusCode(200)
               .body("id", Matchers.notNullValue())
               .body("name", Matchers.equalTo(boardName))
               .log()
               .all();

       JSONObject jsonObject = new JSONObject(response.getBody().asString());
       boardId = jsonObject.getString("id");
    }

    @DisplayName("Step 2 - Add a new list for related board")
    @ParameterizedTest(name = "Create a list named as {0}")
    @ValueSource(strings = "TestList")
    public void createList(String listName) {
        Response response = listServices.createList("/1/lists/", listName, boardId);
        response.then()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo(listName))
                .log()
                .all();

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        listId = jsonObject.getString("id");
    }

    @DisplayName("Step 3 - Add a new card for related list")
    @ParameterizedTest(name = "Create a new card named as {0}")
    @ValueSource(strings = {"TestCard1", "TestCard2"})
    public void createCards(String cardName) {
        Response response = cardServices.createCard("/1/cards/", cardName, listId);
        response.then()
                .statusCode(200)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo(cardName))
                .log()
                .all();

        JSONObject jsonObject = new JSONObject(response.getBody().asString());
        cardIds.add(jsonObject.getString("id"));
    }

    @DisplayName("Step 4 - Update a randomly selected card")
    @ParameterizedTest
    @ValueSource(strings = "TestCard3")
    public void updateARandomCard(String newCardName) {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, cardIds.size());
        String id = cardIds.get(randomIndex);
        Response response = cardServices.updateCard("/1/cards/", id, newCardName);
        response.then()
                .statusCode(200)
                .body("id", Matchers.equalTo(id))
                .body("name", Matchers.equalTo(newCardName))
                .log()
                .all();
    }

    @DisplayName("Step 5 - Delete all created cards")
    @ParameterizedTest
    @MethodSource("getStringStream")
    public void deleteAllCards(String id) {
        Response response = cardServices.deleteCard("/1/cards/", id);
        response.then()
                .statusCode(200);
    }

    @DisplayName("Step 6 - Check cards deletion")
    @ParameterizedTest
    @MethodSource("getStringStream")
    public void shouldCardsBeDeleted(String id) {
        Response response = cardServices.getACard("/1/cards/", id);
        response.then()
                .statusCode(404)
                .body(Matchers.containsString("The requested resource was not found."));
    }

    @DisplayName("Step 7 - Delete all created boards")
    @Test
    public void deleteBoard() {
        Response response = boardServices.deleteBoard("/1/boards/", boardId);
        response.then()
                .statusCode(200);
    }

    @DisplayName("Step 8 - Check board deletion")
    @Test
    public void shouldBoardBeDeleted() {
        Response response = boardServices.getBoard("/1/boards/", boardId);
        response.then()
                .statusCode(404)
                .body(Matchers.containsString("The requested resource was not found."));
    }
}
