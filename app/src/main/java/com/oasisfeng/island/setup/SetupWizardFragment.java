package com.oasisfeng.island.setup;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.setupwizardlib.SetupWizardLayout;
import com.android.setupwizardlib.view.NavigationBar;
import com.oasisfeng.android.os.Bundles;
import com.oasisfeng.island.R;
import com.oasisfeng.island.databinding.SetupWizardBinding;

/**
 * Setup wizard
 *
 * Created by Oasis on 2016/9/8.
 */
public class SetupWizardFragment extends Fragment implements NavigationBar.NavigationBarListener {

	// Tag for creating this fragment. This tag can be used to retrieve this fragment.
	public static final String FRAGMENT_TAG = "SetupManagementFragment";
	private static final String EXTRA_VIEW_MODEL = "vm";

	@Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mViewModel = savedInstanceState.getParcelable(EXTRA_VIEW_MODEL);
		} else {
			final Bundle args = getArguments();
			final SetupViewModel vm = args != null ? args.getParcelable(null) : null;
			mViewModel = vm != null ? vm : new SetupViewModel();
		}

		mContainerViewId = container.getId();
		final SetupWizardBinding binding = SetupWizardBinding.inflate(inflater, container, false);
		binding.setSetup(mViewModel);
		final View view = binding.getRoot();
		final SetupWizardLayout layout = (SetupWizardLayout) view.findViewById(R.id.setup_wizard_layout);

		final NavigationBar nav_bar = layout.getNavigationBar();
		nav_bar.setNavigationBarListener(this);
		setButtonText(nav_bar.getBackButton(), mViewModel.button_back);
		setButtonText(nav_bar.getNextButton(), mViewModel.button_next);
		if (mViewModel.require_scroll_to_bottom) layout.requireScrollToBottom();
//		mViewModel.button_back.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() { @Override public void onPropertyChanged(final Observable observable, final int i) {
//			setButtonText(button_back, mViewModel.button_back);
//		}});
//		mViewModel.button_next.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() { @Override public void onPropertyChanged(final Observable observable, final int i) {
//			setButtonText(button_next, mViewModel.button_next);
//		}});

		return view;
	}

	private static void setButtonText(final Button button, final int text) {
		if (text == 0) return;
		button.setEnabled(text != -1);
		if (text != -1) button.setText(text);
	}

	@Override public void onSaveInstanceState(final Bundle out) {
		out.putParcelable(EXTRA_VIEW_MODEL, mViewModel);
		super.onSaveInstanceState(out);
	}

	@Override public void onNavigateBack() {
//		if (mViewModel.onNavigateBack()) return;
		getActivity().onBackPressed();
	}

	@Override public void onNavigateNext() {
		final SetupViewModel next_vm = mViewModel.onNavigateNext(getActivity());
		if (next_vm == null) return;
		final SetupWizardFragment next_fragment = new SetupWizardFragment();
		next_fragment.setArguments(Bundles.build(b -> b.putParcelable(null, next_vm)));
		getFragmentManager().beginTransaction()
				.setCustomAnimations(R.animator.slide_next_in, R.animator.slide_next_out, R.animator.slide_back_in, R.animator.slide_back_out)
				.addToBackStack(null).replace(mContainerViewId, next_fragment).commit();
	}

	@Override public void onActivityResult(final int request, final int result, final Intent data) {
		SetupViewModel.onActivityResult(getActivity(), request, result);
	}

	private int mContainerViewId;
	private SetupViewModel mViewModel;
}