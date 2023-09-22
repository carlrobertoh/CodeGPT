/*******************************************************************************
 * Copyright (c) 2021 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v2.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package ee.carlrobert.codegpt.telemetry.core.service.segment;

import java.util.Objects;

/**
 * Traits that the segment broker sends with am identify event.
 */
public class IdentifyTraits {
        private final String locale;
        private final String timezone;
        private final String osName;
        private final String osVersion;
        private final String osDistribution;

        public IdentifyTraits(String locale, String timezone, String osName, String osVersion, String osDistribution) {
            this.locale = locale;
            this.timezone = timezone;
            this.osName = osName;
            this.osVersion = osVersion;
            this.osDistribution = osDistribution;
        }

        public String getLocale() {
            return locale;
        }

        public String getTimezone() {
            return timezone;
        }

        public String getOsName() {
            return osName;
        }

        public String getOsVersion() {
            return osVersion;
        }

        public String getOsDistribution() {
            return osDistribution;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof IdentifyTraits)) return false;
            IdentifyTraits identifyTraits = (IdentifyTraits) o;
            return Objects.equals(locale, identifyTraits.locale)
                    && Objects.equals(timezone, identifyTraits.timezone)
                    && Objects.equals(osName, identifyTraits.osName)
                    && Objects.equals(osVersion, identifyTraits.osVersion)
                    && Objects.equals(osDistribution, identifyTraits.osDistribution);
        }

        @Override
        public int hashCode() {
            return Objects.hash(locale, timezone, osName, osVersion, osDistribution);
        }
    }
