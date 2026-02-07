package pl.edu.go.client;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/** Tests server message handling without opening a window or a network connection. */
public class GoBoardDemoTest {

    private GoBoardDemo ui;

    @BeforeClass
    public static void initJavaFX() {
        Platform.startup(() -> {});
    }

    @Before
    public void setUp() throws Exception {
        runFx(() -> {
            ui = new GoBoardDemo();
            ui.turnLabel = new Label();
            ui.colorLabel = new Label();
            ui.passLabel = new Label();
            ui.acceptButton = new Button();
            ui.continueButton = new Button();
            ui.playGameStoryButton = new Button();
            ui.botButton = new Button();
            ui.replayId = new TextField();
            ui.myCapturedLabel = new Label();
            ui.opponentCapturedLabel = new Label();
            ui.handleServerMessage("START BLACK");
        });
        runFx(() -> {});
    }

    @Test
    public void handleServerMessage_START_updatesPlayerColor() throws Exception {
        runFx(() -> ui.handleServerMessage("START BLACK"));
        runFx(() -> assertEquals("Your Color: Czarny", ui.colorLabel.getText()));
    }

    @Test
    public void handleServerMessage_PASS_updatesTurn() throws Exception {
        runFx(() -> ui.handleServerMessage("PASS BLACK"));
        runFx(() -> assertEquals("Opponent's Turn", ui.turnLabel.getText()));
    }

    @Test
    public void handleServerMessage_GAME_END_enablesReplayAndShowsScore() throws Exception {
        runFx(() -> ui.handleServerMessage("GAME_END 10 5 BLACK"));
        runFx(() -> {
            assertFalse(ui.playGameStoryButton.isDisabled());
            assertFalse(ui.replayId.isDisabled());
            assertEquals("You: 10", ui.myCapturedLabel.getText());
            assertEquals("Opponent: 5", ui.opponentCapturedLabel.getText());
        });
    }

    @Test
    public void handleServerMessage_RESIGN_finishesDisplay() throws Exception {
        runFx(() -> ui.handleServerMessage("RESIGN WHITE"));
        runFx(() -> assertEquals("Game Over", ui.turnLabel.getText()));
    }

    private static void runFx(Runnable action) throws Exception {
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(10, TimeUnit.SECONDS);
    }
}
