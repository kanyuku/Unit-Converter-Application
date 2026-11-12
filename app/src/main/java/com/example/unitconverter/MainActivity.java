package com.example.unitconverter;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class MainActivity extends Activity {
    private static final String[] CATEGORIES = {"Length", "Weight", "Volume"};

    private static final UnitOption[][] UNITS = {
            {
                    new UnitOption("Millimetre (mm)", "mm", 0.001),
                    new UnitOption("Centimetre (cm)", "cm", 0.01),
                    new UnitOption("Metre (m)", "m", 1.0),
                    new UnitOption("Kilometre (km)", "km", 1000.0),
                    new UnitOption("Inch (in)", "in", 0.0254),
                    new UnitOption("Foot (ft)", "ft", 0.3048),
                    new UnitOption("Yard (yd)", "yd", 0.9144),
                    new UnitOption("Mile (mi)", "mi", 1609.344)
            },
            {
                    new UnitOption("Milligram (mg)", "mg", 0.000001),
                    new UnitOption("Gram (g)", "g", 0.001),
                    new UnitOption("Kilogram (kg)", "kg", 1.0),
                    new UnitOption("Ounce (oz)", "oz", 0.028349523125),
                    new UnitOption("Pound (lb)", "lb", 0.45359237)
            },
            {
                    new UnitOption("Millilitre (mL)", "mL", 0.001),
                    new UnitOption("Litre (L)", "L", 1.0),
                    new UnitOption("Teaspoon (tsp)", "tsp", 0.00492892159375),
                    new UnitOption("Tablespoon (tbsp)", "tbsp", 0.01478676478125),
                    new UnitOption("US cup (cup)", "cup", 0.2365882365),
                    new UnitOption("US gallon (gal)", "gal", 3.785411784)
            }
    };

    private final DecimalFormat resultFormat = new DecimalFormat("#,##0.######");
    private Spinner categorySpinner;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private EditText valueInput;
    private TextView resultValue;
    private TextView resultUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 35) {
            View root = findViewById(R.id.root_content);
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                int safeInsets = WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout();
                Insets padding = insets.getInsets(safeInsets);
                view.setPadding(padding.left, padding.top, padding.right, padding.bottom);
                return insets;
            });
        }

        categorySpinner = findViewById(R.id.category_spinner);
        fromSpinner = findViewById(R.id.from_spinner);
        toSpinner = findViewById(R.id.to_spinner);
        valueInput = findViewById(R.id.value_input);
        resultValue = findViewById(R.id.result_value);
        resultUnit = findViewById(R.id.result_unit);
        Button convertButton = findViewById(R.id.convert_button);
        TextView privacyPolicyLink = findViewById(R.id.privacy_policy_link);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, CATEGORIES);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // A category is always selected.
            }
        });

        convertButton.setOnClickListener(view -> convertValue());
        privacyPolicyLink.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle(R.string.privacy_policy_title)
                .setMessage(R.string.privacy_policy_content)
                .setPositiveButton(android.R.string.ok, null)
                .show());
        valueInput.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                convertValue();
                return true;
            }
            return false;
        });
    }

    private void updateUnitSpinners(int categoryIndex) {
        ArrayAdapter<UnitOption> unitAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, UNITS[categoryIndex]);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(unitAdapter);
        toSpinner.setAdapter(unitAdapter);
        fromSpinner.setSelection(0);
        toSpinner.setSelection(1);
        clearResult();
    }

    private void convertValue() {
        String enteredValue = valueInput.getText().toString().trim();
        if (enteredValue.isEmpty()) {
            showInputError();
            return;
        }

        final double value;
        try {
            value = Double.parseDouble(enteredValue);
        } catch (NumberFormatException exception) {
            showInputError();
            return;
        }

        if (!Double.isFinite(value)) {
            showInputError();
            return;
        }

        valueInput.setError(null);
        UnitOption from = (UnitOption) fromSpinner.getSelectedItem();
        UnitOption to = (UnitOption) toSpinner.getSelectedItem();
        if (from == null || to == null) {
            return;
        }

        double convertedValue = value * from.toBase / to.toBase;
        resultValue.setText(resultFormat.format(convertedValue));
        resultUnit.setText(to.symbol);
    }

    private void showInputError() {
        clearResult();
        Toast.makeText(this, R.string.input_error, Toast.LENGTH_SHORT).show();
        valueInput.setError(getString(R.string.input_error_short));
        valueInput.requestFocus();
    }

    private void clearResult() {
        resultValue.setText(R.string.result_placeholder);
        resultUnit.setText("");
    }

    private static final class UnitOption {
        final String label;
        final String symbol;
        final double toBase;

        UnitOption(String label, String symbol, double toBase) {
            this.label = label;
            this.symbol = symbol;
            this.toBase = toBase;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
