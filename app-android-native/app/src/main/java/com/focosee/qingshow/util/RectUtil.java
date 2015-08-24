package com.focosee.qingshow.util;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/7/10.
 */
public class RectUtil {
    public static List<Rect> clipRect(Rect targetRect, Rect rect) {
        if (rect.contains(targetRect)) {
            return null;
        }
        List<Rect> rects = new ArrayList<>();

        if (rect.right < targetRect.left || rect.top > targetRect.bottom || rect.left > targetRect.right || rect.bottom < targetRect.top) {
            rects.add(targetRect);
            return rects;
        }

        Rect interRect = getRectIntersection(targetRect, rect);

        if (interRect.left > targetRect.left) {
            rects.add(new Rect(targetRect.left, targetRect.top, interRect.left, targetRect.bottom));
            targetRect = new Rect(interRect.left, targetRect.top, targetRect.right, targetRect.bottom);
        }

        if (interRect.top > targetRect.top) {
            rects.add(new Rect(targetRect.left, targetRect.top, targetRect.right, interRect.top));
            targetRect = new Rect(targetRect.left, interRect.top, targetRect.right, targetRect.bottom);
        }

        if (interRect.right < targetRect.right) {
            rects.add(new Rect(interRect.right, targetRect.top, targetRect.right, targetRect.bottom));
            targetRect = new Rect(targetRect.left, targetRect.top, interRect.right, targetRect.bottom);
        }

        if (interRect.bottom < targetRect.bottom) {
            rects.add(new Rect(targetRect.left, interRect.bottom, targetRect.right, targetRect.bottom));
        }

        return rects;
    }

    public static Rect getRectIntersection(Rect targetRect, Rect rect) {
        Rect resultRect = new Rect();

        if (rect.right < targetRect.left || rect.top > targetRect.bottom || rect.left > targetRect.right || rect.bottom < targetRect.top) {
            return null;
        }

        if (targetRect.left > rect.left) {
            resultRect.left = targetRect.left;
        } else {
            resultRect.left = rect.left;
        }

        if (targetRect.top > rect.top) {
            resultRect.top = targetRect.top;
        } else {
            resultRect.top = rect.top;
        }

        if (targetRect.right < rect.right) {
            resultRect.right = targetRect.right;
        } else {
            resultRect.right = rect.right;
        }

        if (targetRect.bottom < rect.bottom) {
            resultRect.bottom = targetRect.bottom;
        } else {
            resultRect.bottom = rect.bottom;
        }

        return resultRect;
    }

    public static float getRectArea(Rect rect) {
        return Math.abs(rect.width()) * Math.abs(rect.height());
    }

}