package com.alphalaneous.Pages.InteractionPages;

import com.alphalaneous.Annotations.OnLoad;
import com.alphalaneous.Components.ConfigCheckbox;
import com.alphalaneous.Components.EditCommandPanel;
import com.alphalaneous.Components.ThemableJComponents.ThemeableJPanel;
import com.alphalaneous.Interactive.TwitchExclusive.Cheers.CheerData;
import com.alphalaneous.Pages.StreamInteractionsPage;
import com.alphalaneous.Pages.CommandPages.ChatPageComponent;
import com.alphalaneous.Utilities.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CheersPage {

    static ChatPageComponent page = new ChatPageComponent();
    static ThemeableJPanel buttonPanel = new ThemeableJPanel();
    static GridBagConstraints gbc = new GridBagConstraints();

    @OnLoad(order = 9)
    public static void init() {

        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridBagLayout());

        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        page.getContentPane().add(buttonPanel, BorderLayout.NORTH);

        StreamInteractionsPage.addPage("$CHEERS_TITLE$", page, CheersPage::load, CheersPage::showEditMenu);
    }

    public static void showEditMenu(){
        showEditMenu(new CheerData(null));
    }
    public static void showEditMenu(CheerData dataParam){

        String title = "$EDIT_CHEER_ACTION$";

        if(dataParam.getName() == null) title = "$ADD_CHEER_ACTION$";

        EditCommandPanel editCommandPanel = new EditCommandPanel(title, dataParam, (kv, d, e) -> {

            CheerData data;

            AtomicBoolean hasName = new AtomicBoolean(false);

            CheerData.getRegisteredCheers().forEach(c -> {
                if(dataParam.getName() == null && kv.get("name").equalsIgnoreCase(c.getName())) hasName.set(true);
            });

            if(hasName.get()){
                e.setTitleLabelError(true);
            }
            else {
                e.setTitleLabelError(false);
                if (d.getName() == null) {
                    data = new CheerData(kv.get("name"));
                    data.register();
                } else {
                    data = (CheerData) d;
                }

                if(!CheerData.isValidRange(kv.get("range"))){
                    e.setRangeLabelError(true);
                }
                else{
                    e.setRangeLabelError(false);

                    Utilities.ifNotNull(kv.get("message"), o -> data.setMessage((String) o));
                    data.setName(kv.get("name"));
                    data.setRange(kv.get("range"));

                    data.save(true);
                    e.close();
                }
            }
        });
        editCommandPanel.addNameInput("$CHEER_ACTION_NAME_INPUT$", "$CHEER_ACTION_NAME_DESC$");
        editCommandPanel.addMessageInput();
        editCommandPanel.addRangeInput();
        editCommandPanel.setBounds(0,0,800,440);

        editCommandPanel.showMenu();
    }

    public static void load(){

        StreamInteractionsPage.disableRightButton(false);
        StreamInteractionsPage.setRightButtonIcon("+");

        buttonPanel.removeAll();
        for(CheerData cheerData : CheerData.getRegisteredCheers()){

            buttonPanel.add(new ConfigCheckbox(cheerData, () -> showEditMenu(cheerData), false), gbc);
            buttonPanel.add(Box.createVerticalStrut(5), gbc);
        }
        buttonPanel.updateUI();
    }
}
