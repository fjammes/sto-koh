package support;

/**
 * <p>Title: Competitive Learning</p>
 * <p>Description: Generic EPOCH helper routine</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: University of Hertfordshire</p>
 * @author Kevin Doherty (e-mail K.A.J.Doherty@herts.ac.uk
 * @version 1.0
 */
import java.util.*;

public class Dealer {
    Object[] _store_;
    LinkedList<Object> _list_ = new LinkedList<Object>();
    int _epoch_;

    public Dealer(Object[] inputs, int epoch) {
        _store_ = inputs;
        _epoch_ = epoch;
        populateList();
    }

    private void populateList() {
        for (int i = 0; i < _store_.length; i++) {
            _list_.add(_store_[i]);
        }
    }

    public Object getNext() {
        if (_epoch_ > 0) {
            if (!_list_.isEmpty()) {
                return _list_.remove((int)(Math.random() * _list_.size()));
            } else {
                populateList();
                _epoch_--;
                return _list_.remove((int)(Math.random() * _list_.size()));
            }
        }
        return null;
    }

    public Object getNextFixed() {
        if (_epoch_ > 0) {
            if (!_list_.isEmpty()) {
                return _list_.removeFirst();
            } else {
                populateList();
                _epoch_--;
                return _list_.removeFirst();
            }
        }
        return null;
    }

    public boolean hasNext() {
        return (_epoch_ > 0);
    }
}
