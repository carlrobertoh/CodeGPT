/*******************************************************************************
 * Copyright (c) 2022 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package ee.carlrobert.codegpt.telemetry.core.service.segment;

import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import ee.carlrobert.codegpt.telemetry.core.util.Directories;
import ee.carlrobert.codegpt.telemetry.core.util.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Persistency for {@link IdentifyTraits}.
 */
public class IdentifyTraitsPersistence {

    public static final IdentifyTraitsPersistence INSTANCE = new IdentifyTraitsPersistence();
    private static final Logger LOGGER = Logger.getInstance(IdentifyTraitsPersistence.class);

    private static final Path FILE = Directories.PATH.resolve("segment-identify-traits.json");

    private IdentifyTraits identifyTraits = null;

    protected IdentifyTraitsPersistence() {}

    public synchronized IdentifyTraits get() {
        if (identifyTraits == null) {
            this.identifyTraits = deserialize(load(FILE));
        }
        return identifyTraits;
    }

    public synchronized void set(IdentifyTraits identifyTraits) {
        if (Objects.equals(identifyTraits, this.identifyTraits)) {
            return;
        }
        this.identifyTraits = identifyTraits;
        String string = null;
        if (identifyTraits != null) {
            string = serialize(identifyTraits);
        }
        save(string, FILE);
    }

    private String serialize(IdentifyTraits identifyTraits) {
        if (identifyTraits == null) {
            return null;
        }
        return new Gson().toJson(identifyTraits);
    }

    private IdentifyTraits deserialize(String identity) {
        if (identity == null) {
            return null;
        }
        return new Gson().fromJson(identity, IdentifyTraits.class);
    }

    private String load(Path file) {
        String event = null;
        try(Stream<String> lines = getLines(file)) {
            event = lines
                    .findAny()
                    .map(String::trim)
                    .orElse(null);
        } catch (IOException e) {
            LOGGER.warn("Could not read identity file at " + file.toAbsolutePath(), e);
        }
        return event;
    }

    /* for testing purposes */
    protected Stream<String> getLines(Path file) throws IOException {
        return Files.lines(file);
    }

    private void save(String event, Path file) {
        try {
            createFileAndParent(file);
            writeFile(event, file);
        } catch (IOException e) {
            LOGGER.warn("Could not write identity to file at " + FILE.toAbsolutePath(), e);
        }
    }

    /* for testing purposes */
    protected void createFileAndParent(Path file) throws IOException {
        FileUtils.createFileAndParent(file);
    }

    /* for testing purposes */
    protected void writeFile(String event, Path file) throws IOException {
        FileUtils.write(event, file);
    }
}
