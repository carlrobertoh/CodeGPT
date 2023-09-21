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
package ee.carlrobert.codegpt.telemetry.ui.preferences;

import com.intellij.ide.DataManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ex.Settings;
import com.intellij.ui.components.JBLabel;
import ee.carlrobert.codegpt.telemetry.ui.utils.JBLabelUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.util.function.Supplier;

public class TelemetryPreferencesUtils {

    private TelemetryPreferencesUtils() {
    }

    public static JLabel createTelemetryComponent(final String plugin, final Supplier<JPanel> panel) {
        JLabel component = new JBLabel("") {
            @Override
            protected HyperlinkListener createHyperlinkListener() {
                return event -> {
                    if (HyperlinkEvent.EventType.ACTIVATED == event.getEventType()) {
                        openTelemetryPreferences(panel);
                    }
                };
            }
        }
                .setCopyable(true)
                .setAllowAutoWrapping(true);
        component.setVerticalTextPosition(SwingConstants.TOP);
        component.setFocusable(false);
        component.setText(JBLabelUtils.toStyledHtml(
                "<br/>Help Red Hat improve " + plugin + " by sending anonymous usage data."
                        + " You can enable or disable it in the <a href=\"\">preferences for Red Hat Telemetry</a>."
                , 70,
                component));
        return component;
    }

    private static void openTelemetryPreferences(Supplier<JPanel> panel) {
        Settings allSettings = Settings.KEY.getData(DataManager.getInstance().getDataContext(panel.get()));
        if (allSettings != null) {
            final Configurable configurable = allSettings.find(TelemetryConfigurable.ID);
            if (configurable != null) {
                allSettings.select(configurable);
            }
        }
    }
}
