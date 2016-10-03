// Generated code from Butter Knife. Do not modify!
package com.ucmap.bluetoothsearch;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class BluetoothFragment$$ViewBinder<T extends com.ucmap.bluetoothsearch.BluetoothFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131492971, "field 'mDeviceName'");
    target.mDeviceName = finder.castView(view, 2131492971, "field 'mDeviceName'");
    view = finder.findRequiredView(source, 2131492986, "field 'mBack' and method 'onClick'");
    target.mBack = finder.castView(view, 2131492986, "field 'mBack'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492987, "field 'mDevice' and method 'onClick'");
    target.mDevice = finder.castView(view, 2131492987, "field 'mDevice'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492948, "field 'mSendButton' and method 'onClick'");
    target.mSendButton = finder.castView(view, 2131492948, "field 'mSendButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131492949, "field 'mInputEdit'");
    target.mInputEdit = finder.castView(view, 2131492949, "field 'mInputEdit'");
    view = finder.findRequiredView(source, 2131492947, "field 'mBottomNa'");
    target.mBottomNa = finder.castView(view, 2131492947, "field 'mBottomNa'");
    view = finder.findRequiredView(source, 2131492950, "field 'mContentRecyclerview'");
    target.mContentRecyclerview = finder.castView(view, 2131492950, "field 'mContentRecyclerview'");
  }

  @Override public void unbind(T target) {
    target.mDeviceName = null;
    target.mBack = null;
    target.mDevice = null;
    target.mSendButton = null;
    target.mInputEdit = null;
    target.mBottomNa = null;
    target.mContentRecyclerview = null;
  }
}
