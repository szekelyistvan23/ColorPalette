package szekelyistvan.com.colorpalette.provider;

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

import android.content.ContentResolver;

/**
 * Uses a custom AsyncQueryHandler, the difference is the added bulk insert method.
 */

public class PaletteAsyncQueryHandler extends com.madrapps.asyncquery.AsyncQueryHandler{
    public PaletteAsyncQueryHandler(ContentResolver cr) {
        super(cr);
    }
}
