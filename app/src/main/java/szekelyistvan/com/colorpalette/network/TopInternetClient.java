package szekelyistvan.com.colorpalette.network;

/*
 * Copyright (C) 2018 Szekely Istvan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import szekelyistvan.com.colorpalette.model.Palette;


/**
 * Interface for Retrofit, the implementation is based on:
 * https://www.youtube.com/watch?v=R4XU8yPzSx0
 */

public interface TopInternetClient {
    String REQUEST_URL = "top?format=json&numResults=100&showPaletteWidths=1";

    @GET(REQUEST_URL)
    Call<List<Palette>> topPalettesData();
}
