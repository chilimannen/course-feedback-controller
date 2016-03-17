import Model.AsyncMasterClient;
import Model.Voting;
import io.vertx.core.Future;

/**
 * Created by Robin on 2016-03-17.
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
