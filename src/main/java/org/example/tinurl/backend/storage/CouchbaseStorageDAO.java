package org.example.tinurl.backend.storage;

import com.couchbase.client.core.env.SeedNode;
import com.couchbase.client.core.error.BucketExistsException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.couchbase.client.java.ClusterOptions.clusterOptions;
import static java.util.Optional.empty;

@Service
public class CouchbaseStorageDAO implements StorageDAO {

    private Collection urlBucketCollection;

    private static final Logger LOG = LoggerFactory.getLogger(CouchbaseStorageDAO.class);

    @Value("${couchbase.url}")
    private String couchbaseUrl;

    @Value("${couchbase.kv_port}")
    private Integer couchbaseKvPort;

    @Value("${couchbase.mgmt_port}")
    private Integer couchbaseManagerPort;

    @PostConstruct
    public void initCollection() {
        final Set<SeedNode> seedNodes = new HashSet<>(Collections.singletonList(
                SeedNode.create(couchbaseUrl,
                        Optional.of(couchbaseKvPort),
                        Optional.of(couchbaseManagerPort))));

        final Cluster cluster = Cluster.connect(seedNodes, clusterOptions("Administrator", "password"));

        try {
            cluster.buckets().createBucket(BucketSettings.create("urlbucket"));
        } catch (BucketExistsException e) {
            LOG.info("Bucket urlbucket already exists");
        }

        final Bucket urlBucket = cluster.bucket("urlbucket");
        urlBucketCollection = urlBucket.defaultCollection();
    }

    @Override
    public Optional<String> originalUrl(String tinyUrl) {
        final GetResult getResult = urlBucketCollection.get(tinyUrl);

        try {
            return Optional.of(getResult.contentAsObject().getString("originalUrl"));
        } catch (DocumentNotFoundException e) {
            LOG.info("Tiny url is not present in DB, url = {}", tinyUrl);
            return empty();
        }
    }

    @Override
    public String put(String tinyUrl, String originalUrl) {
        final MutationResult upsertResult = urlBucketCollection.upsert(
                tinyUrl,
                JsonObject.create().put("originalUrl", originalUrl)
        );

        return tinyUrl;
    }
}
