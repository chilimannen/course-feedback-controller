import Model.AsyncMasterClient;
import Model.Voting;
import io.vertx.core.Future;

/**
 * @author Robin Duda
 *
 * A mock of a connection to a set of masters.
 */
public class MasterClientMock implements AsyncMasterClient {
    @Override
    public void create(Future<Void> future, Voting voting) {
        future.complete();
    }

    @Override
    public void terminate(Future<Void> future, Voting voting) {
        future.complete();
    }
}
