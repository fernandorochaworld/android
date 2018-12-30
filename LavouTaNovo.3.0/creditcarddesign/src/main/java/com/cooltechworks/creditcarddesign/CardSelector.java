package com.cooltechworks.creditcarddesign;

import java.util.regex.Pattern;

/**
 * Created by Harish on 01/01/16.
 */
public class CardSelector {

    public static final CardSelector VISA = new CardSelector(R.drawable.card_color_round_rect_purple, R.drawable.chip, R.drawable.chip_inner, android.R.color.transparent, R.drawable.ic_billing_visa_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector MASTER = new CardSelector(R.drawable.card_color_round_rect_pink, R.drawable.chip_yellow, R.drawable.chip_yellow_inner, android.R.color.transparent, R.drawable.ic_billing_mastercard_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector AMEX = new CardSelector(R.drawable.card_color_round_rect_green, android.R.color.transparent, android.R.color.transparent, R.drawable.img_amex_center_face, R.drawable.ic_billing_amex_logo1, CardSelector.CVV_LENGHT_AMEX);
    public static final CardSelector DINERS = new CardSelector(R.drawable.card_color_round_rect_green, android.R.color.transparent, android.R.color.transparent, android.R.color.transparent, R.drawable.ic_billing_diners_logo, CardSelector.CVV_LENGHT_DEFAULT);
    public static final CardSelector DEFAULT = new CardSelector(R.drawable.card_color_round_rect_default, R.drawable.chip, R.drawable.chip_inner, android.R.color.transparent, android.R.color.transparent, CardSelector.CVV_LENGHT_DEFAULT);

    public static final char PREFIX_AMEX = '3';
    private static final char PREFIX_VISA = '4';
    private static final char PREFIX_MASTER = '5';

    public static final int CVV_LENGHT_DEFAULT = 3;
    public static final int CVV_LENGHT_AMEX = 4;


    private int mResCardId;
    private int mResChipOuterId;
    private int mResChipInnerId;
    private int mResCenterImageId;
    private int mResLogoId;
    private int mCvvLength = CVV_LENGHT_DEFAULT;

    public CardSelector(int mDrawableCard, int mDrawableChipOuter, int mDrawableChipInner, int mDrawableCenterImage, int logoId, int cvvLength) {
        this.mResCardId = mDrawableCard;
        this.mResChipOuterId = mDrawableChipOuter;
        this.mResChipInnerId = mDrawableChipInner;
        this.mResCenterImageId = mDrawableCenterImage;
        this.mResLogoId = logoId;
        this.mCvvLength = cvvLength;
    }

    public int getResCardId() {
        return mResCardId;
    }

    public void setResCardId(int mResCardId) {
        this.mResCardId = mResCardId;
    }

    public int getResChipOuterId() {
        return mResChipOuterId;
    }

    public void setResChipOuterId(int mResChipOuterId) {
        this.mResChipOuterId = mResChipOuterId;
    }

    public int getResChipInnerId() {
        return mResChipInnerId;
    }

    public void setResChipInnerId(int mResChipInnerId) {
        this.mResChipInnerId = mResChipInnerId;
    }

    public int getResCenterImageId() {
        return mResCenterImageId;
    }

    public void setResCenterImageId(int mResCenterImageId) {
        this.mResCenterImageId = mResCenterImageId;
    }

    public int getResLogoId() {
        return mResLogoId;
    }

    public void setResLogoId(int mResLogoId) {
        this.mResLogoId = mResLogoId;
    }

    public int getCvvLength() {
        return mCvvLength;
    }

    public void setCvvLength(int mCvvLength) {
        this.mCvvLength = mCvvLength;
    }

    public static CardSelector selectCardPattern(String number) {
        Pattern patternAmex = Pattern.compile("^3[47][0-9]{2}$");
        Pattern patternDiners = Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{1}");
        Pattern patternVisa = Pattern.compile("^4[0-9]{3}");
        Pattern patternMaster = Pattern.compile("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)");

        if(patternAmex.matcher(number).find()) {
            return AMEX;
        } else if (patternDiners.matcher(number).find()) {
            return DINERS;
        } else if (patternVisa.matcher(number).find()) {
            return VISA;
        } else if (patternMaster.matcher(number).find()) {
            return MASTER;
        } else {
            return DEFAULT;
        }
    }

    public static CardSelector selectCard(String cardNumber) {
        if (cardNumber != null && cardNumber.length() >= 4) {
            CardSelector selector = selectCardPattern(cardNumber.substring(0,4));
            CreditCardUtils.BRAND = cardNumber.charAt(0);

            if (selector != DEFAULT) {
                int[] drawables = {R.drawable.card_color_round_rect_brown, R.drawable.card_color_round_rect_green, R.drawable.card_color_round_rect_pink, R.drawable.card_color_round_rect_purple, R.drawable.card_color_round_rect_blue};
                int hash = cardNumber.substring(0, 3).hashCode();

                if (hash < 0) {
                    hash = hash * -1;
                }

                int index = hash % drawables.length;

                int chipIndex = hash % 3;
                int[] chipOuter = {R.drawable.chip, R.drawable.chip_yellow, android.R.color.transparent};
                int[] chipInner = {R.drawable.chip_inner, R.drawable.chip_yellow_inner, android.R.color.transparent};

                selector.setResCardId(drawables[index]);
                selector.setResChipOuterId(chipOuter[chipIndex]);
                selector.setResChipInnerId(chipInner[chipIndex]);

                return selector;
            }
        }

        return DEFAULT;
    }
}
