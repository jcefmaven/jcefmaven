// Copyright (c) 2014 The Chromium Embedded Framework Authors. All rights
// reserved. Use of this source code is governed by a BSD-style license that
// can be found in the LICENSE file.

package me.friwi.jcefmaven.detailed.ui;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel {
    private final JProgressBar progressBar_;
    private final JLabel status_field_;

    public StatusPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        add(Box.createHorizontalStrut(5));
        add(Box.createHorizontalStrut(5));

        progressBar_ = new JProgressBar();
        Dimension progressBarSize = progressBar_.getMaximumSize();
        progressBarSize.width = 100;
        progressBar_.setMinimumSize(progressBarSize);
        progressBar_.setMaximumSize(progressBarSize);
        add(progressBar_);
        add(Box.createHorizontalStrut(5));

        status_field_ = new JLabel("Info");
        status_field_.setAlignmentX(LEFT_ALIGNMENT);
        add(status_field_);
        add(Box.createHorizontalStrut(5));
        add(Box.createVerticalStrut(21));
    }

    public void setIsInProgress(boolean inProgress) {
        progressBar_.setIndeterminate(inProgress);
    }

    public void setStatusText(String text) {
        status_field_.setText(text);
    }
}
