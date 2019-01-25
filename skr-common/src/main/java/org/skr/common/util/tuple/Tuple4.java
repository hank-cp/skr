package org.skr.common.util.tuple;

/**
 * Scala style Tuple for Java
 */
public class Tuple4<T0, T1, T2, T3> {

    public final T0 _0;
    public final T1 _1;
    public final T2 _2;
    public final T3 _3;

    public Tuple4(T0 _0, T1 _1, T2 _2, T3 _3) {
        this._0 = _0;
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple4 another = (Tuple4) o;

        return !(_0 != null ? !_0.equals(another._0) : another._0 != null)
            && !(_1 != null ? !_1.equals(another._1) : another._1 != null)
            && !(_2 != null ? !_2.equals(another._2) : another._2 != null)
            && !(_3 != null ? !_3.equals(another._3) : another._3 != null);

    }

    @Override
    public int hashCode() {
        int result = _0 != null ? _0.hashCode() : 0;
        result = 31 * result + (_1 != null ? _1.hashCode() : 0);
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        result = 31 * result + (_3 != null ? _3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + _0 + ',' + _1 + ',' + _2 + ',' + _3 + ')';
    }
}
