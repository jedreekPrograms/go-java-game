package pl.edu.go.server;

import org.junit.*;
import org.mockito.MockedStatic;
import pl.edu.go.model.Color;
import pl.edu.go.server.commandInterfaces.*;
import pl.edu.go.server.networkInterfaces.ClientConnection;
import pl.edu.go.server.persistence.PersistenceApplication;
import pl.edu.go.server.persistence.entity.GameEntity;
import pl.edu.go.server.persistence.entity.MoveType;
import pl.edu.go.server.persistence.service.GamePersistenceService;

import static org.mockito.Mockito.*;

public class GameSessionTest {

    private ClientConnection white;
    private ClientConnection black;
    private CommandRegistry registry;
    private GameSession session;

    @Before
    public void setup() {
        white = mock(ClientConnection.class);
        black = mock(ClientConnection.class);
        registry = new CommandRegistry();
        registry.register("MOVE", new MoveCommand());
        registry.register("PASS", new PassCommand());
        registry.register("RESIGN", new ResignCommand());
        session = new GameSession(white, black, 9, registry);
    }

    @Test
    public void testPlayerColorsAssigned() {
        Assert.assertEquals(Color.WHITE, session.getPlayerColor(white));
        Assert.assertEquals(Color.BLACK, session.getPlayerColor(black));
    }

    @Test
    public void testMoveOutOfTurnRejected() {
        session.onMessage(white, "MOVE 1 1");
        verify(white).send(startsWith("ERROR"));
    }

    @Test
    public void testValidMoveNotifiesOpponent() {
        GamePersistenceService persistence = mock(GamePersistenceService.class);
        GameEntity entity = mock(GameEntity.class);
        session.setGameEntity(entity);
        try (MockedStatic<PersistenceApplication> application = mockStatic(PersistenceApplication.class)) {
            application.when(() -> PersistenceApplication.getBean(GamePersistenceService.class))
                    .thenReturn(persistence);
            session.start(); // BLACK starts
            session.onMessage(black, "MOVE 2 2");

            verify(black).send(contains("MOVE BLACK 2 2"));
            verify(black, atLeastOnce()).send(contains("BOARD"));
            verify(white).send(contains("MOVE BLACK 2 2"));
            verify(white, atLeastOnce()).send(contains("BOARD"));
            verify(persistence).saveMove(entity, 1, 2, 2, Color.BLACK, MoveType.MOVE);
        }
    }

}
