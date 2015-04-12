package it.droidcon.b_nox.data;

import lombok.Value;

/**
 * Created by demiurgo on 4/11/15.
 */

@Value
public final class ArtDetail {

    String id;
    String title;
    String author;
    String date;
    String place;
    String description;

    String[][] images;
    String[][] videos;
    String[][] audios;

}
