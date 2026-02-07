package pl.edu.go.server.commandInterfaces;

import org.junit.*;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import pl.edu.go.server.commandInterfaces.AcceptCommand;
import pl.edu.go.server.GameSession;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.model.*;
import pl.edu.go.model.TerritoryCalculator.GameResult;
import pl.edu.go.server.persistence.PersistenceApplication;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.service.GamePersistenceService;

public class AcceptCommandTest {

    private AcceptCommand command;
    private GameSession session;
    private GameState game;
    private ClientConnection sender;

    @Before
    public void setUp() {
        command = new AcceptCommand();
        session = mock(GameSession.class);
        game = mock(GameState.class);
        sender = mock(ClientConnection.class);

        when(session.getGame()).thenReturn(game);
        when(session.getPlayerColor(sender)).thenReturn(Color.BLACK);
    }

    @Test
    public void shouldRejectWhenGameNotStopped() {
        when(game.getStatus()).thenReturn(GameState.Status.PLAYING);

        boolean result = command.execute(new String[]{}, session, sender);

        assertFalse(result);
        verify(sender).send("ERROR Game is not in scoring phase");
    }

    @Test
    public void shouldAcceptFirstPlayerOnly() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(game.accept(Color.BLACK)).thenReturn(false);

        boolean result = command.execute(new String[]{}, session, sender);

        assertTrue(result);
        verify(session).sendToBoth("PLAYER_ACCEPTED BLACK");
        verify(game, never()).confirmEndGame();
    }


    @Test
    public void shouldEndGameWhenBothAccepted() {
        when(game.getStatus()).thenReturn(GameState.Status.STOPPED);
        when(game.accept(Color.BLACK)).thenReturn(true);
        when(game.getFinalResult()).thenReturn(new GameResult(10.0, 8.5, 6.5, "BLACK"));
        GameEntity entity = mock(GameEntity.class);
        when(session.getGameEntity()).thenReturn(entity);
        GamePersistenceService persistence = mock(GamePersistenceService.class);

        try (MockedStatic<PersistenceApplication> application = mockStatic(PersistenceApplication.class)) {
            application.when(() -> PersistenceApplication.getBean(GamePersistenceService.class))
                    .thenReturn(persistence);
            boolean result = command.execute(new String[]{}, session, sender);

            assertTrue(result);
            verify(game).confirmEndGame();
            verify(session).sendToBoth("GAME_END 10.0 8.5 BLACK");
            verify(persistence).finishGame(entity, "BLACK");
            // The connection remains available for replaying the saved game.
            verify(session, never()).endSession();
        }
    }

}
