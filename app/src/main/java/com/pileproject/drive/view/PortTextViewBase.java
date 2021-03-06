/**
 * Copyright (C) 2011-2017 The PILE Developers <pile-dev@googlegroups.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pileproject.drive.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.pileproject.drive.R;

/**
 * A base {@link TextView} which expresses a port.
 */
public abstract class PortTextViewBase extends TextView {
    private final String mPortName;
    private final String mPortType;
    protected String mDeviceType;

    public PortTextViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray tar = context.obtainStyledAttributes(attrs, R.styleable.PortTextViewBase);
        mPortName = tar.getString(R.styleable.PortTextViewBase_portName);
        mPortType = tar.getString(R.styleable.PortTextViewBase_portType);
        tar.recycle();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startDrag(null, new DragShadowBuilder(v), v, 0);
                return true;
            }
        });
    }

    /**
     * Swaps the device type of ports.
     *
     * @param lhs a port
     * @param rhs another port
     */
    public static void swapDeviceType(PortTextViewBase lhs, PortTextViewBase rhs) {
        String lhsAttachmentType = lhs.getDeviceType();
        lhs.setDeviceType(rhs.getDeviceType());
        rhs.setDeviceType(lhsAttachmentType);
    }

    private void swap(PortTextViewBase lhs, PortTextViewBase rhs) {
        if (lhs.mDeviceType == null && rhs.mDeviceType == null) {
            // do nothing
        }
        else if (lhs.mDeviceType == null /* && rhs.mDeviceType != null */) {
            savePortConnection(lhs.mPortName, rhs.mDeviceType);
            removePortConnection(rhs.mPortName);
        }
        else if (/* lhs.mDeviceType != null && */ rhs.mDeviceType == null) {
            savePortConnection(rhs.mPortName, lhs.mDeviceType);
            removePortConnection(lhs.mPortName);
        }
        else /* if (lhs.mDeviceType != null && rhs.mDeviceType != null) */ {
            savePortConnection(lhs.mPortName, rhs.mDeviceType);
            savePortConnection(rhs.mPortName, lhs.mDeviceType);
        }

        swapDeviceType(lhs, this);
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        final int action = event.getAction();
        final PortTextViewBase localState = (PortTextViewBase) event.getLocalState();

        if (action == DragEvent.ACTION_DRAG_STARTED) {
            return true;
        }
        else if (action == DragEvent.ACTION_DROP) {
            // match the port type (e.g. "motor" and "motor", "sensor" and "sensor")
            if (localState.mPortType.equals(this.mPortType)) {

                MediaPlayer soundEffectOfMovingBlock = MediaPlayer.create(getContext(), R.raw.pon);
                soundEffectOfMovingBlock.start(); // play sound
                soundEffectOfMovingBlock.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                });

                swap(localState, this);
            }
            return true;
        }
        return false;
    }

    /**
     * Saves a port connection setting with a device.
     *
     * @param port a port in string
     * @param device a device in string
     */
    protected abstract void savePortConnection(String port, String device);

    /**
     * Removes a port connection setting.
     *
     * @param port a port in string
     */
    protected abstract void removePortConnection(String port);

    /**
     * Gets the type of device.
     *
     * @return a device type of this port in string
     */
    public abstract String getDeviceType();

    /**
     * Sets the type of device.
     *
     * @param deviceType a device type of this port
     */
    public abstract void setDeviceType(String deviceType);

    /**
     * Gets the name of port.
     *
     * @return the name of this port in string
     */
    public String getPortName() {
        return mPortName;
    }
}
