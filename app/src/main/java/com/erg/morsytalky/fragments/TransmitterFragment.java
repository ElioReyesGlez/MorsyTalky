package com.erg.morsytalky.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.erg.morsytalky.R;
import com.erg.morsytalky.controller.CameraEngine;
import com.erg.morsytalky.controller.helpers.MessagesHelper;
import com.erg.morsytalky.controller.helpers.MorseHelper;
import com.erg.morsytalky.interfaces.OnCameraEngine;
import com.erg.morsytalky.util.SuperUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static androidx.annotation.Dimension.SP;
import static com.erg.morsytalky.util.Constants.DASH_DELAY;
import static com.erg.morsytalky.util.Constants.DOT_DELAY;
import static com.erg.morsytalky.util.Constants.INTER_WORD_DELAY;
import static com.erg.morsytalky.util.Constants.REGEX_SPACE;
import static com.erg.morsytalky.util.Constants.SPACE;

public class TransmitterFragment extends Fragment implements View.OnClickListener,
        OnCameraEngine {

    private static final String TAG = "TransmitterFragment";

    private View rootView;
    private ImageButton btnTransmit;
    private TextInputEditText editTextMessage;
    private TextInputLayout textInputLayoutMessage;
    private ImageView ivFlashIndicator;
    private LinearLayout boxesContainer;
    private ScrollView scrollViewBoxesContainer;
    private ArrayList<TextView> boxesList;
    private CameraEngine cameraEngine;
    private boolean isTransmitting = false;
    private Animation scaleUp, scaleDown;
    private int wordsCont = 0;
    private Thread blinker;

    public TransmitterFragment() {
    }

    public static TransmitterFragment newInstance() {
        return new TransmitterFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transmitter, container, false);
        setUpView();
        return rootView;
    }

    private void setUpView() {

        /*Binding Views*/
        btnTransmit = rootView.findViewById(R.id.ib_transmit);
        ivFlashIndicator = rootView.findViewById(R.id.iv_flash_indicator);
        editTextMessage = rootView.findViewById(R.id.tie_message);
        textInputLayoutMessage = rootView.findViewById(R.id.til_message);
        boxesContainer = rootView.findViewById(R.id.ll_boxes_container);
        scrollViewBoxesContainer = rootView.findViewById(R.id.scroll_view_boxes_container);

        /*Animations*/
        scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up_plus);
        scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down);

        /*Setting OnClickListener*/
        btnTransmit.setOnClickListener(this);

        /*Initializations*/
        boxesList = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        SuperUtils.vibrate(requireActivity());
        if (v.getId() == R.id.ib_transmit) {
            startTransmissionFlow();
        }
    }

    private void startTransmissionFlow() {
        /*Getting message from user*/
        final String message = Objects.requireNonNull(editTextMessage.getText()).toString();
        /*verifying user entrance its correct*/
        if (!message.isEmpty()) {
            initTransmission(message);
        } else { // message incorrect
            textInputLayoutMessage.setError(getString(R.string.required_field));
            editTextMessage.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        /*Returning to default state*/
                        textInputLayoutMessage.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void initTransmission(final String message) {
        boxCreator(message);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    String morse = MorseHelper.getMorseFromString(message);
                    if (!morse.isEmpty()) {
                        char[] charArray = morse.toCharArray();
                        highlightWord();
                        for (char token : charArray) {
                            Log.d(TAG, "run: isTransmitting: " + isTransmitting);
                            if (isTransmitting) {
                                switch (token) {
                                    case '.':
                                        cameraEngine.switchFlash(ivFlashIndicator);
                                        TimeUnit.MILLISECONDS.sleep(DOT_DELAY);
                                        cameraEngine.switchFlash(ivFlashIndicator);
                                        TimeUnit.MILLISECONDS.sleep(DOT_DELAY);
                                        break;
                                    case '-':
                                        cameraEngine.switchFlash(ivFlashIndicator);
                                        TimeUnit.MILLISECONDS.sleep(DASH_DELAY);
                                        cameraEngine.switchFlash(ivFlashIndicator);
                                        TimeUnit.MILLISECONDS.sleep(DASH_DELAY);
                                        break;
                                    case SPACE:
                                        TimeUnit.MILLISECONDS.sleep(INTER_WORD_DELAY);
                                        highlightWord();
                                        break;
                                }
                            } else {
                                return;
                            }
                        }
                        interruptTransmission(false);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e(TAG, "doInBackground: ERROR: " + e.getMessage());
                    MessagesHelper.showInfoMessageError(requireActivity(),
                            "Something bad happened; Transmission Interrupted");
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        };

        blinker = new Thread(task);
        if (!isTransmitting) {
            startTransmission();
        } else {
            interruptTransmission(true);
        }
    }

    private void boxCreator(String message) {

        String[] splitMessage = message.split(REGEX_SPACE);
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        boxParams.setMargins(8, 4, 8, 4);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        StringBuilder textInRow = new StringBuilder();
        LinearLayout row = new LinearLayout(getContext());
        row.setLayoutParams(rowParams);
        int textWith = SuperUtils.getTextWith(textInRow.toString(), requireContext());
        final int SCREEN_WITH = SuperUtils.getDisplayWidth(requireActivity());

        for (int i = 0; i < splitMessage.length; ) {
            while (textWith < SCREEN_WITH - (SCREEN_WITH / 2) && i < splitMessage.length) {
                /*Creating Box TextView*/
                TextView boxTextView = new TextView(requireContext());
                boxTextView.setId(i);
                boxTextView.setBackground(ContextCompat.getDrawable(requireContext(),
                        R.drawable.background_gray));
                boxTextView.setElevation(2);
                boxTextView.setMaxLines(1);
                boxTextView.setEllipsize(TextUtils.TruncateAt.END);
                boxTextView.setLayoutParams(boxParams);
                boxTextView.setPadding(8, 8, 8, 8);
                boxTextView.setTextSize(SP, 16);

                String word = splitMessage[i];
                boxTextView.setText(word);

                textInRow.append(word).append(SPACE);
                textWith = SuperUtils.getTextWith(textInRow.toString(), requireContext());
                boxesList.add(boxTextView);
                row.addView(boxTextView);
                i++;
            }

            LinearLayout.LayoutParams scrollViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            scrollViewParams.setMargins(2, 2, 2, 2);
            HorizontalScrollView horizontalScrollView = new HorizontalScrollView(requireContext());
            horizontalScrollView.setFillViewport(true);
            horizontalScrollView.setHorizontalScrollBarEnabled(false);
            horizontalScrollView.setLayoutParams(scrollViewParams);
            horizontalScrollView.addView(row);
            boxesContainer.addView(horizontalScrollView);

            /*Resetting Everything*/
            row = new LinearLayout(requireContext());
            row.setLayoutParams(rowParams);
            textInRow = new StringBuilder();
            textWith = 0;
        }
    }

    private void highlightWord() {
        /*Getting current Word into TextView Box*/
        if (wordsCont < boxesList.size()) {
            final TextView textView = boxesList.get(wordsCont);
            textView.post(new Runnable() {
                @Override
                public void run() {
                    textView.setBackgroundResource(R.drawable.background_accent);
                }
            });
            wordsCont++;
        }
    }

    private void interruptTransmission(boolean interruptedFlag) { //ToDo
        isTransmitting = false;
        blinker.interrupt();
        btnTransmit.post(new Runnable() {
            @Override
            public void run() {
                btnTransmit.setImageResource(R.drawable.ic_send);
            }
        });
        scrollViewBoxesContainer.post(new Runnable() {
            @Override
            public void run() {
                SuperUtils.hideView(scaleDown, scrollViewBoxesContainer);
            }
        });
        textInputLayoutMessage.post(new Runnable() {
            @Override
            public void run() {
                SuperUtils.showView(scaleUp, textInputLayoutMessage);
            }
        });
        boxesContainer.post(new Runnable() {
            @Override
            public void run() {
                boxesContainer.removeAllViews();
            }
        });

        // Resetting words boxes list
        boxesList.clear();
        // Resetting words boxes Cont
        wordsCont = 0;

        if (interruptedFlag) {
            MessagesHelper.showInfoMessageWarning(
                    requireActivity(),
                    getString(R.string.transmission_interrupted));
        } else {
            MessagesHelper.showInfoMessage(
                    requireActivity(),
                    getString(R.string.transmission_finished));
        }
        Log.d(TAG, "stopTransmission: Interrupted!!");
    }


    private void startTransmission() {
        isTransmitting = true;
        textInputLayoutMessage.post(new Runnable() {
            @Override
            public void run() {
                SuperUtils.hideView(scaleDown, textInputLayoutMessage);
            }
        });
        scrollViewBoxesContainer.post(new Runnable() {
            @Override
            public void run() {
                SuperUtils.showView(scaleUp, scrollViewBoxesContainer);
            }
        });
        btnTransmit.post(new Runnable() {
            @Override
            public void run() {
                btnTransmit.setImageResource(R.drawable.ic_stop);
            }
        });
        if (blinker != null && blinker.isAlive()) {
            blinker.interrupt();
        }
        // Starting Blinker Thread
        assert blinker != null;
        blinker.start();

        MessagesHelper.showInfoMessage(
                requireActivity(),
                getString(R.string.transmission_started_msg));
        Log.d(TAG, "startTransmission: Start!");
    }

    @Override
    public void bindCameraEngine(CameraEngine cameraEngine) {
        Log.d(TAG, "bindCameraEngine: Bound!");
        this.cameraEngine = cameraEngine;
    }

}