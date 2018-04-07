package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.MoveInfoOnlineFetcher;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.scanlogic.ScanContainer;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.PowerTableDataAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;


public class MovesetFraction extends Fraction {

    @BindView(R.id.sortableTable)
    SortableTableView sortableTable;
    @BindView(R.id.movesetConstrainLayout)
    ConstraintLayout movesetConstrainLayout;

    private Pokefly pokefly;
    private ArrayList<MovesetData> movesets = new ArrayList();
    private IVScanResult ivScanResult;

    public MovesetFraction(@NonNull Pokefly pokefly, @NonNull IVScanResult ivScanResult) {
        this.pokefly = pokefly;
        this.ivScanResult = ivScanResult;
    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_moveset;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);
        loadMovesetData();
        //if (movesets.size() <= 0) {
        //    createDummyData();
        //}


        setupTableHeader();
        setupDataSorting();
        addDataToTable();
        //fixTableConstrainLayoutHeight();
        sortableTable.sort(2); //default to sorting column  3 (atk)

    }

    /**
     * For some reason "wrap content" makes the constraintview more than 100DP too long, so here's a method to set it
     * manually.
     */
    private void fixTableConstrainLayoutHeight() {
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, pokefly.getResources()
                .getDisplayMetrics());

        ViewGroup.LayoutParams params = movesetConstrainLayout.getLayoutParams();
        //constant length for header + length for every data row.
        params.height = (int) ((dp * 100) + (dp * movesets.size() * 14));
        movesetConstrainLayout.setLayoutParams(params);


        ViewGroup.LayoutParams params2 = sortableTable.getLayoutParams();
        //constant length for header + length for every data row.
        params2.height = (int) ((dp * 50) + (dp * movesets.size() * 20));
        sortableTable.setLayoutParams(params2);
    }

    /**
     * Adds comparators to the columns for attack and defence values.
     */
    private void setupDataSorting() {
        sortableTable.setColumnComparator(2, new MovesetData.AtkComparator());
        sortableTable.setColumnComparator(3, new MovesetData.DefComparator());
    }

    private void setupTableHeader() {

        String[] tableHeaders = {"Quick", "Charge", "Atk", "Def"};
        sortableTable.setHeaderAdapter(new SimpleTableHeaderAdapter(pokefly, tableHeaders));


        TableColumnWeightModel columnModel = new TableColumnWeightModel(4);
        columnModel.setColumnWeight(0, 3);
        columnModel.setColumnWeight(1, 3);
        columnModel.setColumnWeight(2, 2);
        columnModel.setColumnWeight(3, 2);
        sortableTable.setColumnModel(columnModel);
    }

    /**
     * Adds the data from the moveset list to the table.
     */
    private void addDataToTable() {
        if (movesets != null) {
            MovesetData[] movesetsArray = new MovesetData[movesets.size()];
            movesetsArray = movesets.toArray(movesetsArray);
            sortableTable.setDataAdapter(new PowerTableDataAdapter(pokefly, movesetsArray));
        }
    }

    private void loadMovesetData() {
        MoveInfoOnlineFetcher onlineFetcher = new MoveInfoOnlineFetcher(pokefly);
        movesets = onlineFetcher.getMovesetData(pokefly, ivScanResult);
    }

    @Override public void onDestroy() {
    }

    @OnClick(R.id.powerUpButton)
    void onPowerUp() {
        pokefly.navigateToPowerUpFraction();
    }

    @OnClick(R.id.ivButton)
    void onMoveset() {
        pokefly.navigateToIVResultFraction();
    }

    @OnClick(R.id.btnBack)
    void onBack() {
        pokefly.navigateToInputFraction();
    }

    @OnClick(R.id.btnClose)
    void onClose() {
        pokefly.closeInfoDialog();
    }

    /**
     * Creates an intent to share the result of the pokemon scan, and closes the overlay.
     */
    @OnClick({R.id.shareWithOtherApp})
    void shareScannedPokemonInformation() {
        PokemonShareHandler communicator = new PokemonShareHandler();
        communicator.spreadResultIntent(pokefly, ScanContainer.scanContainer.currScan, pokefly.pokemonUniqueID);
        pokefly.closeInfoDialog();
    }
}
