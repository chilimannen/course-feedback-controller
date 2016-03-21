import Configuration.Configuration;
import Controller.WebServer;
import Model.*;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.Timeout;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;

/**
 * @author Robin Duda
 *         <p/>
 *         Tests the api methods for the controller service.
 *         <p/>
 *         /list - list all votings with specified owner.
 *         /create - create a new voting
 *         /terminate - remove a voting and everything attached to it.
 */

@RunWith(VertxUnitRunner.class)
public class APITest {
    private Vertx vertx;
    private TokenFactory tokenFactory;
    private TokenFactory ownerFactory;
    private static final String USERNAME = "gosu";

    @Rule
    public Timeout timeout = Timeout.seconds(15);

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(new WebServer(new VotingDBMock(), new MasterClientMock()), context.asyncAssertSuccess());
        tokenFactory = new TokenFactory(Configuration.SERVER_SECRET);
        ownerFactory = new TokenFactory(Configuration.CLIENT_SECRET);
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }


    @Test
    public void testCreateVoting(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.WEB_PORT, "localhost", "/api/create", response -> {
                    context.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getValidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    private JsonObject getVotingConfiguration() {
        return new JsonObject()
                .put("topic", "test-voting")
                .put("owner", USERNAME)
                .put("duration",
                        new JsonObject()
                                .put("begin", Instant.now().getEpochSecond())
                                .put("end", Instant.now().getEpochSecond() + 10))
                .put("options",
                        new JsonArray()
                                .add(new JsonObject()
                                        .put("name", "query 1")
                                        .put("values",
                                                new JsonArray()
                                                        .add("value 1")
                                                        .add("value 2")
                                                        .add("value 3")))
                                .add(new JsonObject()
                                        .put("name", "query 2")
                                        .put("values",
                                                new JsonArray()
                                                        .add("value a")
                                                        .add("value b")
                                                        .add("value c"))));
    }

    @Test
    public void testTerminateVoting(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.WEB_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("owner", USERNAME)
                        .put("token", getValidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }


    @Test
    public void testListVotingInProgress(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.WEB_PORT, "localhost", "/api/list", response -> {
                    context.assertEquals(HttpResponseStatus.OK.code(), response.statusCode());

                    response.bodyHandler(body -> {
                        VotingList votings = (VotingList) Serializer.unpack(body.toJsonObject(), VotingList.class);
                        Voting voting = votings.getVotings().get(0);

                        context.assertEquals(1, votings.getVotings().size());
                        context.assertEquals(2, votings.getVotings().get(0).getOptions().size());

                        context.assertEquals("Vote #1", voting.getTopic());
                        context.assertEquals("id", voting.getId());
                        context.assertEquals("gosu", voting.getOwner());
                        context.assertEquals("q1", voting.getOptions().get(0).getName());
                        context.assertEquals("q2", voting.getOptions().get(1).getName());
                        context.assertTrue(voting.getOptions().get(0).getValues().contains("a"));
                        context.assertTrue(voting.getOptions().get(0).getValues().contains("b"));
                        context.assertTrue(voting.getOptions().get(0).getValues().contains("c"));

                        async.complete();
                    });

                }).end(
                new JsonObject()
                        .put("owner", USERNAME)
                        .put("token", getValidToken())
                        .encode());
    }

    @Test
    public void testCreateVotingRequiresToken(TestContext context) {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.WEB_PORT, "localhost", "/api/create", response -> {
                    context.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("token", getInvalidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    @Test
    public void testListVotingRequiresToken(TestContext context) throws TokenException {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.WEB_PORT, "localhost", "/api/list", response -> {
                    context.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("owner", USERNAME)
                        .put("token", getInvalidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }

    @Test
    public void testTerminateVotingRequiresToken(TestContext context) {
        Async async = context.async();

        vertx.createHttpClient()
                .post(Configuration.WEB_PORT, "localhost", "/api/terminate", response -> {
                    context.assertEquals(HttpResponseStatus.UNAUTHORIZED.code(), response.statusCode());
                    async.complete();
                }).end(
                new JsonObject()
                        .put("owner", USERNAME)
                        .put("token", getInvalidToken())
                        .put("voting", getVotingConfiguration())
                        .encode());
    }


    @Test
    public void testAsync(TestContext context) {
        Async async = context.async();
        async.complete();
    }


    private JsonObject getInvalidToken() {
        return new JsonObject()
                .put("key", "invalid")
                .put("domain", Configuration.SERVER_NAME)
                .put("expiry", Instant.now().getEpochSecond() + 90);
    }

    private JsonObject getValidToken() throws TokenException {
        return new JsonObject(Serializer.pack(new Token(tokenFactory, Configuration.SERVER_NAME)));
    }
}
