package com.lodsve.boot.mongodb;

import com.lodsve.boot.mongodb.client.MongoClientDelegate;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.internal.ChangeStreamIterableImpl;
import com.mongodb.client.internal.ListDatabasesIterableImpl;
import com.mongodb.client.internal.MongoDatabaseImpl;
import com.mongodb.client.internal.OperationExecutor;
import com.mongodb.connection.*;
import com.mongodb.internal.client.model.changestream.ChangeStreamLevel;
import com.mongodb.internal.connection.Cluster;
import com.mongodb.internal.connection.DefaultClusterFactory;
import com.mongodb.lang.Nullable;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.Collections;
import java.util.List;

import static com.mongodb.assertions.Assertions.notNull;
import static com.mongodb.internal.event.EventListenerHelper.getCommandListener;
import static org.bson.internal.CodecRegistryHelper.createRegistry;

/**
 * .
 *
 * @author <a href="mailto:sunhao.java@gmail.com">sunhao(sunhao.java@gmail.com)</a>
 */
public class DynamicMongoClient implements MongoClient {
    private final MongoClientSettings settings;
    private final MongoClientDelegate delegate;

    public DynamicMongoClient(final MongoClientSettings settings, @Nullable final MongoDriverInformation mongoDriverInformation) {
        this(createCluster(settings, mongoDriverInformation), settings, null);
    }

    public DynamicMongoClient(final Cluster cluster, final MongoClientSettings settings,
                              @Nullable final OperationExecutor operationExecutor) {
        this.settings = notNull("settings", settings);
        this.delegate = new MongoClientDelegate(notNull("cluster", cluster),
            createRegistry(settings.getCodecRegistry(), settings.getUuidRepresentation()), this, operationExecutor);
    }

    private static Cluster createCluster(final MongoClientSettings settings,
                                         @Nullable final MongoDriverInformation mongoDriverInformation) {
        notNull("settings", settings);
        return new DefaultClusterFactory().createCluster(settings.getClusterSettings(), settings.getServerSettings(),
            settings.getConnectionPoolSettings(), getStreamFactory(settings, false), getStreamFactory(settings, true),
            settings.getCredential(), getCommandListener(settings.getCommandListeners()), settings.getApplicationName(),
            mongoDriverInformation, settings.getCompressorList());
    }

    private static StreamFactory getStreamFactory(final MongoClientSettings settings, final boolean isHeartbeat) {
        StreamFactoryFactory streamFactoryFactory = settings.getStreamFactoryFactory();
        SocketSettings socketSettings = isHeartbeat ? settings.getHeartbeatSocketSettings() : settings.getSocketSettings();
        if (streamFactoryFactory == null) {
            return new SocketStreamFactory(socketSettings, settings.getSslSettings());
        } else {
            return streamFactoryFactory.create(socketSettings, settings.getSslSettings());
        }
    }

    @Override
    public MongoDatabase getDatabase(final String databaseName) {
        return new MongoDatabaseImpl(databaseName, delegate.getCodecRegistry(), settings.getReadPreference(), settings.getWriteConcern(),
            settings.getRetryWrites(), settings.getRetryReads(), settings.getReadConcern(),
            settings.getUuidRepresentation(), delegate.getOperationExecutor());
    }

    @Override
    public MongoIterable<String> listDatabaseNames() {
        return createListDatabaseNamesIterable(null);
    }

    @Override
    public MongoIterable<String> listDatabaseNames(final ClientSession clientSession) {
        notNull("clientSession", clientSession);
        return createListDatabaseNamesIterable(clientSession);
    }

    @Override
    public ListDatabasesIterable<Document> listDatabases() {
        return listDatabases(Document.class);
    }

    @Override
    public <T> ListDatabasesIterable<T> listDatabases(final Class<T> clazz) {
        return createListDatabasesIterable(null, clazz);
    }

    @Override
    public ListDatabasesIterable<Document> listDatabases(final ClientSession clientSession) {
        return listDatabases(clientSession, Document.class);
    }

    @Override
    public <T> ListDatabasesIterable<T> listDatabases(final ClientSession clientSession, final Class<T> clazz) {
        notNull("clientSession", clientSession);
        return createListDatabasesIterable(clientSession, clazz);
    }

    @Override
    public ClientSession startSession() {
        return startSession(ClientSessionOptions
            .builder()
            .defaultTransactionOptions(TransactionOptions.builder()
                .readConcern(settings.getReadConcern())
                .writeConcern(settings.getWriteConcern())
                .build())
            .build());
    }

    @Override
    public ClientSession startSession(final ClientSessionOptions options) {
        ClientSession clientSession = delegate.createClientSession(notNull("options", options),
            settings.getReadConcern(), settings.getWriteConcern(), settings.getReadPreference());
        if (clientSession == null) {
            throw new MongoClientException("Sessions are not supported by the MongoDB cluster to which this client is connected");
        }
        return clientSession;
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public ChangeStreamIterable<Document> watch() {
        return watch(Collections.<Bson>emptyList());
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(final Class<TResult> resultClass) {
        return watch(Collections.<Bson>emptyList(), resultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(final List<? extends Bson> pipeline) {
        return watch(pipeline, Document.class);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(final List<? extends Bson> pipeline, final Class<TResult> resultClass) {
        return createChangeStreamIterable(null, pipeline, resultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(final ClientSession clientSession) {
        return watch(clientSession, Collections.<Bson>emptyList(), Document.class);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(final ClientSession clientSession, final Class<TResult> resultClass) {
        return watch(clientSession, Collections.<Bson>emptyList(), resultClass);
    }

    @Override
    public ChangeStreamIterable<Document> watch(final ClientSession clientSession, final List<? extends Bson> pipeline) {
        return watch(clientSession, pipeline, Document.class);
    }

    @Override
    public <TResult> ChangeStreamIterable<TResult> watch(final ClientSession clientSession, final List<? extends Bson> pipeline,
                                                         final Class<TResult> resultClass) {
        notNull("clientSession", clientSession);
        return createChangeStreamIterable(clientSession, pipeline, resultClass);
    }

    @Override
    public ClusterDescription getClusterDescription() {
        return delegate.getCluster().getCurrentDescription();
    }

    private <TResult> ChangeStreamIterable<TResult> createChangeStreamIterable(@Nullable final ClientSession clientSession,
                                                                               final List<? extends Bson> pipeline,
                                                                               final Class<TResult> resultClass) {
        return new ChangeStreamIterableImpl<>(clientSession, "admin", settings.getCodecRegistry(), settings.getReadPreference(),
            settings.getReadConcern(), delegate.getOperationExecutor(),
            pipeline, resultClass, ChangeStreamLevel.CLIENT, settings.getRetryReads());
    }

    public Cluster getCluster() {
        return delegate.getCluster();
    }

    public CodecRegistry getCodecRegistry() {
        return delegate.getCodecRegistry();
    }

    private <T> ListDatabasesIterable<T> createListDatabasesIterable(@Nullable final ClientSession clientSession, final Class<T> clazz) {
        return new ListDatabasesIterableImpl<>(clientSession, clazz, delegate.getCodecRegistry(), ReadPreference.primary(),
            delegate.getOperationExecutor(), settings.getRetryReads());
    }

    private MongoIterable<String> createListDatabaseNamesIterable(final @Nullable ClientSession clientSession) {
        return createListDatabasesIterable(clientSession, BsonDocument.class).nameOnly(true).map(new Function<BsonDocument, String>() {
            @Override
            public String apply(final BsonDocument result) {
                return result.getString("name").getValue();
            }
        });
    }
}
