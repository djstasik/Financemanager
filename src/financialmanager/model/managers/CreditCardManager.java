package financialmanager.model.managers;

import financialmanager.model.entities.CreditCard;
import java.util.ArrayList;
import java.util.List;

public class CreditCardManager {
    private final List<CreditCard> creditCards = new ArrayList<>();
    private final List<CardChangeListener> listeners = new ArrayList<>();

    public interface CardChangeListener {
        void onCardsChanged(List<CreditCard> cards);
    }

    public void addListener(CardChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CardChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        List<CreditCard> copy = new ArrayList<>(creditCards);
        for (CardChangeListener listener : listeners) {
            listener.onCardsChanged(copy);
        }
    }

    public void addCard(CreditCard card) {
        creditCards.add(card);
        notifyListeners();
    }

    public void removeCard(CreditCard card) {
        creditCards.remove(card);
        notifyListeners();
    }

    public void removeCardById(String id) {
        creditCards.removeIf(card -> card.getId().equals(id));
        notifyListeners();
    }

    public void updateCard(CreditCard card) {
        for (int i = 0; i < creditCards.size(); i++) {
            if (creditCards.get(i).getId().equals(card.getId())) {
                creditCards.set(i, card);
                break;
            }
        }
        notifyListeners();
    }

    public List<CreditCard> getAllCards() {
        return new ArrayList<>(creditCards);
    }

    public CreditCard getCardById(String id) {
        return creditCards.stream()
                .filter(card -> card.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public double getTotalDebt() {
        return creditCards.stream()
                .mapToDouble(CreditCard::getCurrentBalance)
                .sum();
    }

    public double getTotalAvailableCredit() {
        return creditCards.stream()
                .mapToDouble(CreditCard::getAvailableCredit)
                .sum();
    }

    public double getTotalCreditLimit() {
        return creditCards.stream()
                .mapToDouble(CreditCard::getCreditLimit)
                .sum();
    }

    public void clear() {
        creditCards.clear();
        notifyListeners();
    }

    public int size() {
        return creditCards.size();
    }
}